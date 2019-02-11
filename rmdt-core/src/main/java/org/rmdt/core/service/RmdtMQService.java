package org.rmdt.core.service;

import org.rmdt.common.config.RmdtConfig;
import org.rmdt.common.domain.Participant;

/**
 * @author luohaipeng
 * RMDT框架消息队列相关服务
 */
public interface RmdtMQService {

    /**
     * 启动事务日志可靠消息相关支持
     */
    void start(RmdtConfig rmdtConfig);

    /**
     * 给当前事务中的每个事务参与者发送可靠消息
     * @param participant
     */
    void sendMessage(Participant participant);
}
