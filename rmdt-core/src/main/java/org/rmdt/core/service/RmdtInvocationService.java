package org.rmdt.core.service;

import org.rmdt.common.config.RmdtConfig;
import org.rmdt.common.domain.Participant;

/**
 * @author luohaipeng
 * 提供使用反射的方式调用方法
 */
public interface RmdtInvocationService {


    /**
     * 调用该方法的情况是队列消息消费监听到有新的消息
     * Participant对象就是消息体反序列化过来的
     * 一个Participant对象就代表一个事务参与者
     * 需要反射调用所使用的信息都包含在Participant对象中
     * @param participant
     * @return
     */
    void invoke(Participant participant) ;

    /**
     * RMDT框架在初始化时，会通过该方法注入用户配置的RmdtConfig对象
     * @param rmdtConfig
     */
    void setRmdtConfig(final RmdtConfig rmdtConfig);
}
