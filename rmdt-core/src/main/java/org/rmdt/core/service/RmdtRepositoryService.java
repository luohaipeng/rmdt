package org.rmdt.core.service;

import org.rmdt.common.config.RmdtConfig;
import org.rmdt.common.domain.Transaction;

import java.util.Date;
import java.util.List;

/**
 * @author luohaipeng
 * 事务日志存储相关服务
 */
public interface RmdtRepositoryService {


    /**
     * 启动事务日志存储相关支持
     */
    void start(RmdtConfig rmdtConfig);

    /**
     * 保存事务
     * @param transaction
     */
    void save(Transaction transaction);


    /**
     * 修改事务
     * @param transaction
     */
    void update(Transaction transaction);

    /**
     * 通过事务id获取事务对象
     * @param transactionId
     * @return
     */
    Transaction getById(String transactionId);

    /**
     * 查询需要恢复的错误事务
     * @param date
     * @return
     */
    List<Transaction> findRecover(Date date,Integer retriedPeriod);
}
















