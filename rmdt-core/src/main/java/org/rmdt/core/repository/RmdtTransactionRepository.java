package org.rmdt.core.repository;

import org.rmdt.common.config.RmdtConfig;
import org.rmdt.common.domain.Transaction;
import org.rmdt.core.serialize.ObjectSerializer;

import java.util.Date;
import java.util.List;

/**
 * @author luohaipeng
 * 事务日志存储技术支持
 */
public interface RmdtTransactionRepository {

    /**
     * 设置一个
     */
    void setObjectSerializer(ObjectSerializer objectSerializer);

    /**
     * 给该Repository起一个名字，用于加载Repository的SPI时，判断用户使用哪种Repository技术
     * @return
     */
    String getRepositoryName();

    /**
     * 初始Repository相关的数据，如获取数据源，创建表结构等等。
     */
    void init(RmdtConfig rmdtConfig);

    /**
     * 插入一条事务日志数据
     * @param transaction
     */
    Integer inster(Transaction transaction);

    /**
     * 更新事务日志数据
     * @param transaction
     */
    Integer update(Transaction transaction);

    /**
     * 通过事务id获取事务对象
     * @param transactionId
     * @return
     */
    Transaction getById(String transactionId);

    /**
     * 查找需要恢复的错误事务
     * @param date
     * @param retriedPeriod
     * @return
     */
    List<Transaction> findRecover(Date date,Integer retriedPeriod);
}
