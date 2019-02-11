package org.rmdt.core.message.impl;

import org.rmdt.annotation.Listener;
import org.rmdt.common.config.RmdtConfig;
import org.rmdt.common.domain.Participant;
import org.rmdt.core.message.RmdtTransactionMessage;
import org.rmdt.core.serialize.ObjectSerializer;
import org.rmdt.core.service.RmdtInvocationService;

import java.util.List;

/**
 * @author luohaipeng
 * 由Kafka提供消息队列服务支持
 */
public class KafkaTransactionMessage implements RmdtTransactionMessage {
    @Override
    public void init(RmdtConfig rmdtConfig) {

    }

    @Override
    public void setObjectSerializer(ObjectSerializer objectSerializer) {

    }

    @Override
    public void setInvocationService(RmdtInvocationService rmdtInvocationService) {

    }

    @Override
    public void send(Participant participant) {

    }

    @Override
    public String getMessageQueueName() {
        return "kafka";
    }

    @Override
    public void listen(List<Listener> listeners) {

    }
}
