package org.rmdt.core.service.impl;

import org.rmdt.common.config.RmdtConfig;
import org.rmdt.common.domain.Transaction;
import org.rmdt.common.enums.TransactionEventEnum;
import org.rmdt.common.thread.RmdtTransactionThreadFactory;
import org.rmdt.core.disruptor.RmdtTransactionEventProducer;
import org.rmdt.core.service.RmdtMQService;
import org.rmdt.core.service.RmdtRepositoryService;
import org.rmdt.core.service.RmdtSchedulerRecoverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author luohaipeng
 */
@Slf4j
@Service
public class RmdtSchedulerRecoverServiceImpl implements RmdtSchedulerRecoverService {

    @Autowired
    private RmdtRepositoryService rmdtRepositoryService;//事务日志数据存储服务

    @Autowired
    private RmdtTransactionEventProducer rmdtTransactionEventProducer;//事务事件发送者服务

    @Autowired
    private RmdtMQService rmdtMQService;//事务日志消息队列服务

    public void schedulerTransactionRecover(RmdtConfig rmdtConfig) {
        new ScheduledThreadPoolExecutor(1, new RmdtTransactionThreadFactory("transactionRecover-thread-"))
                .scheduleWithFixedDelay(() -> {
                    List<Transaction> transactionList = rmdtRepositoryService.findRecover(convertDate(rmdtConfig),rmdtConfig.getRetriedCount());
                    if (transactionList != null && transactionList.size() > 0) {
                        transactionList.forEach(transaction -> {
                            log.debug("定时任务重新发送MQ可靠消息尝试重新执行出错的事务：" + transaction.getTransactionId());
                            transaction.getParticipants().forEach(participant -> {
                                rmdtMQService.sendMessage(participant);
                            });
                            //添加一次发送MQ消息次数,并发布更新事务事件
                            transaction.setSendMessageCount(transaction.getSendMessageCount() + 1);
                            rmdtTransactionEventProducer.publish(transaction,TransactionEventEnum.UPDATE.getCode());

                        });
                    }
                }, 30, rmdtConfig.getRetriedPeriod(), TimeUnit.SECONDS);

    }

    private Date convertDate(RmdtConfig rmdtConfig) {
        return new Date(System.currentTimeMillis() - (rmdtConfig.getRetriedDelayTime() * 1000));
    }

}
