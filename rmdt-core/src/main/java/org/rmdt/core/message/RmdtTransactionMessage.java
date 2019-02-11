package org.rmdt.core.message;

import org.rmdt.annotation.Listener;
import org.rmdt.common.config.RmdtConfig;
import org.rmdt.common.domain.Participant;
import org.rmdt.core.serialize.ObjectSerializer;
import org.rmdt.core.service.RmdtInvocationService;

import java.util.List;

/**
 * @author luohaipeng
 * 发送可靠消息接口服务类
 */
public interface RmdtTransactionMessage {

    /**
     * 初始化MQ相关配置
     */
    void init(RmdtConfig rmdtConfig) ;

    /**
     * 在框架初始化消息队列SPI后，会通过该方法设置对象序列化工具
     * @param objectSerializer
     */
    void setObjectSerializer(ObjectSerializer objectSerializer);


    /**
     * 在框架初始化消息队列SPI后，会通过该方法设置方法反射调用的服务
     * @param rmdtInvocationService
     */
    void setInvocationService(RmdtInvocationService rmdtInvocationService);

    /**
     * 给消息队列支持技术起一个名字,用于到时候用配置对象中的messageQueueName匹配到具体使用哪个消息队列
     * @return
     */
    String getMessageQueueName();

    /**
     * 给事务参与者发送可靠消息
     * 在极端情况下，事务参与者所在的服务器宕机了，重启服务器后消费消息，重新执行方法，达到事务一致的作用
     */
    void send(Participant participant);

    /**
     * 需要监听的消息队列地点
     * @param listeners  需要监听的消息队列地点
     */
    void listen(List<Listener> listeners);
}
