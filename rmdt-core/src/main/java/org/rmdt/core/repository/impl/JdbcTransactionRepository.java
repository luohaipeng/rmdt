package org.rmdt.core.repository.impl;

import org.rmdt.common.config.RmdtConfig;
import org.rmdt.common.config.repository.JdbcRepositoryConfig;
import org.rmdt.common.constant.RmdtConstant;
import org.rmdt.common.domain.Participant;
import org.rmdt.common.domain.Transaction;
import org.rmdt.common.enums.TransactionStatusEnum;
import org.rmdt.core.repository.RmdtTransactionRepository;
import org.rmdt.core.serialize.ObjectSerializer;
import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author luohaipeng
 * jdbc事务日志存储支持
 */
@Slf4j
public class JdbcTransactionRepository implements RmdtTransactionRepository {

    private ObjectSerializer objectSerializer;
    private DruidDataSource dataSource;
    private String tableName;

    @Override
    public void setObjectSerializer(ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }

    @Override
    public String getRepositoryName() {
        return "jdbc";
    }

    @Override
    public void init(RmdtConfig rmdtConfig) {
        dataSource = new DruidDataSource();
        //把抽象的存储技术配置类强转为自己对应的配置类
        JdbcRepositoryConfig jdbcConfig = (JdbcRepositoryConfig) rmdtConfig.getRepositoryConfig();
        dataSource.setDriverClassName(jdbcConfig.getDriverClassName());
        dataSource.setUrl(jdbcConfig.getUrl());
        dataSource.setUsername(jdbcConfig.getUsername());
        dataSource.setPassword(jdbcConfig.getPassword());
        dataSource.setInitialSize(jdbcConfig.getInitialSize());
        dataSource.setMaxActive(jdbcConfig.getMaxActive());
        dataSource.setMinIdle(jdbcConfig.getMinIdle());
        dataSource.setMaxWait(jdbcConfig.getMaxWait());
        dataSource.setValidationQuery(jdbcConfig.getValidationQuery());
        dataSource.setTestOnBorrow(jdbcConfig.getTestOnBorrow());
        dataSource.setTestOnReturn(jdbcConfig.getTestOnReturn());
        dataSource.setTestWhileIdle(jdbcConfig.getTestWhileIdle());
        dataSource.setPoolPreparedStatements(jdbcConfig.getPoolPreparedStatements());
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(jdbcConfig.getMaxPoolPreparedStatementPerConnectionSize());
        tableName = RmdtConstant.FRAMEWORK_NAME+"_"+jdbcConfig.getReposirotyModelSuffix().replaceAll("-","_");
        //创建表
        executeUpdate(buildCreateTableSql(tableName));
    }

    @Override
    public Integer inster(Transaction transaction) {
        StringBuilder sql = new StringBuilder()
                .append("insert into ")
                .append(tableName)
                .append("(transaction_id,target_class,target_method,retried_count,send_message_count,create_time,last_time,status,invocation,role,error_message)")
                .append(" values(?,?,?,?,?,?,?,?,?,?,?)");
        try {
            //把事务参与者集合序列化成二进制数据
            byte[] participantSerialize = objectSerializer.serialize(transaction.getParticipants());
            return executeUpdate(sql.toString(),
                    transaction.getTransactionId(),
                    transaction.getTargetClass(),
                    transaction.getTargetMethod(),
                    transaction.getRetriedCount(),
                    transaction.getSendMessageCount(),
                    transaction.getCreateTime(),
                    transaction.getLastTime(),
                    transaction.getStatus(),
                    participantSerialize,
                    transaction.getRole(),
                    transaction.getErrorMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return RmdtConstant.JDBC_ERROR;
        }
    }

    @Override
    public Integer update(Transaction transaction) {
        transaction.setLastTime(new Date());
        String sql = "update " + tableName + " set last_time = ?,retried_count = ?,send_message_count = ?,invocation = ?,status = ?,error_message = ? where transaction_id = ?";
        try {
            byte[] participantSerialize = objectSerializer.serialize(transaction.getParticipants());
            return executeUpdate(sql,
                    transaction.getLastTime(),
                    transaction.getRetriedCount(),
                    transaction.getSendMessageCount(),
                    participantSerialize,
                    transaction.getStatus(),
                    transaction.getErrorMessage(),
                    transaction.getTransactionId());
        } catch (Exception e) {
            e.printStackTrace();
            return RmdtConstant.JDBC_ERROR;
        }
    }

    @Override
    public Transaction getById(String transactionId) {
        String selectSql = "select * from " + tableName + " where transaction_id=?";
        List<Map<String, Object>> list = executeQuery(selectSql, transactionId);
        if (Objects.nonNull(list) && list.size() > 0) {
            return list.stream().filter(Objects::nonNull).map(this::buildByResultMap).findFirst().get();
        }
        return null;
    }

    @Override
    public List<Transaction> findRecover(Date date,Integer retriedPeriod) {
        String selectSql = "select * from " + tableName + " where last_time < ?  and status = " + TransactionStatusEnum.FAILURE.getCode() + " and send_message_count < " + retriedPeriod;
        List<Map<String, Object>> list = executeQuery(selectSql, date);
        if (Objects.nonNull(list) && list.size() > 0) {
            return list.stream().filter(Objects::nonNull).map(this::buildByResultMap).collect(Collectors.toList());
        }
        return null;
    }


    /**
     * 构建创建表的SQL语句
     * @param tableName
     * @return
     */
    private String buildCreateTableSql(String tableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS `")
                .append(tableName).append("` (\n")
                .append("  `transaction_id` varchar(64) NOT NULL,\n")
                .append("  `target_class` varchar(256) ,\n")
                .append("  `target_method` varchar(128) ,\n")
                .append("  `retried_count` int(3) NOT NULL,\n")
                .append("  `send_message_count` int(3) NOT NULL,\n")
                .append("  `create_time` datetime NOT NULL,\n")
                .append("  `last_time` datetime NOT NULL,\n")
                .append("  `status` int(2) NOT NULL,\n")
                .append("  `invocation` longblob,\n")
                .append("  `role` int(2) NOT NULL,\n")
                .append("  `error_message` text ,\n")
                .append("   PRIMARY KEY (`transaction_id`)\n")
                .append(")");
        return sql.toString();
    }

    /**
     * 通过jdbc查询结果装换为Transaction事务对象
     * @param map
     * @return
     */
    private Transaction buildByResultMap(final Map<String, Object> map) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId((String) map.get("transaction_id"));
        transaction.setRetriedCount((Integer) map.get("retried_count"));
        transaction.setSendMessageCount((Integer) map.get("send_message_count"));
        transaction.setCreateTime((Date) map.get("create_time"));
        transaction.setLastTime((Date) map.get("last_time"));
        transaction.setStatus((Integer) map.get("status"));
        transaction.setRole((Integer) map.get("role"));
        transaction.setErrorMessage((String) map.get("error_message"));
        transaction.setTargetClass((String) map.get("target_class"));
        transaction.setTargetMethod((String) map.get("target_method"));
        byte[] bytes = (byte[]) map.get("invocation");
        try {
            List<Participant> participants = objectSerializer.deSerialize(bytes, CopyOnWriteArrayList.class);
            transaction.setParticipants(participants);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transaction;
    }
    /**
     * jdbc执行更新操作
     * @param sql
     * @param params
     * @return
     */
    private int executeUpdate(final String sql, final Object... params) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            log.error("executeUpdate-> " + e.getMessage());
        }
        return 0;
    }

    /**
     * jdbc执行查询操作
     * @param sql
     * @param params
     * @return
     */
    private List<Map<String, Object>> executeQuery(final String sql, final Object... params) {
        List<Map<String, Object>> list = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                int columnCount = md.getColumnCount();
                list = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> rowData = new HashMap<>(16);
                    for (int i = 1; i <= columnCount; i++) {
                        rowData.put(md.getColumnName(i), rs.getObject(i));
                    }
                    list.add(rowData);
                }
            }
        } catch (SQLException e) {
            log.error("executeQuery-> " + e.getMessage());
        }
        return list;
    }
}
