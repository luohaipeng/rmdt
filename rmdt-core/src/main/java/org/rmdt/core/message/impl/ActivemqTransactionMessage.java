package org.rmdt.core.message.impl;

import org.rmdt.annotation.Listener;
import org.rmdt.common.config.RmdtConfig;
import org.rmdt.common.config.message.ActivemqMessageConfig;
import org.rmdt.common.domain.Participant;
import org.rmdt.common.enums.MessageEnum;
import org.rmdt.core.message.RmdtTransactionMessage;
import org.rmdt.core.serialize.ObjectSerializer;
import org.rmdt.core.service.RmdtInvocationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.*;
import java.util.List;
import java.util.Objects;

/**
 * @author luohaipeng
 * ActiveMQ作为消息队列服务提供支持
 */
@Slf4j
public class ActivemqTransactionMessage implements RmdtTransactionMessage {

    private ObjectSerializer objectSerializer;
    private RmdtInvocationService rmdtInvocationService;
    private Connection connection;

    @Override
    public String getMessageQueueName() {
        return "activemq";
    }


    @Override
    public void init(RmdtConfig rmdtConfig) {
        try {
            //强转为ActivemqMessageConfig，ActivemqTransactionMessage和ActivemqMessageConfig是通过SPI扩展的，
            //所以使用框架的开发者肯定知道需要强转为自己定义的ActivemqMessageConfig配置对象
            ActivemqMessageConfig messageConfig = (ActivemqMessageConfig) rmdtConfig.getMessageConfig();
            //创建连接工厂
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(messageConfig.getUserName(),messageConfig.getPassword(),messageConfig.getUrl());
            //从连接工厂创建连接
            connection = connectionFactory.createConnection();
            //开启连接
            connection.start();
        }catch (JMSException e){
            e.printStackTrace();
        }
    }

    @Override
    public void setObjectSerializer(ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }

    @Override
    public void setInvocationService(RmdtInvocationService rmdtInvocationService) {
        this.rmdtInvocationService = rmdtInvocationService;
    }

    /**
     * 提供activemq技术具体如何发消息的处理
     * @param participant
     */
    @Override
    public void send(Participant participant) {
        Session session = null;
        try {
            //创建session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //创建消息发送的地点
            Destination destination = null;
            if(Objects.equals(participant.getMessageDomain(),MessageEnum.P2P.getCode()))
                destination = new ActiveMQQueue(participant.getDestination());
            else
                destination = new ActiveMQTopic(participant.getDestination());
            //创建消息发送者
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            //创建消息对象
            BytesMessage message = session.createBytesMessage();
            log.debug("开始往"+participant.getDestination()+"地点发送，消息长度为："+objectSerializer.serialize(participant).length);
            message.writeBytes(objectSerializer.serialize(participant));
            //发送消息
            producer.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //关闭会话
            try {
                session.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 提供activemq具体如何监听消息的处理
     * @param listeners  需要监听的消息队列地点
     */
    @Override
    public void listen(List<Listener> listeners) {
        //遍历出所有需要监听的消息队列地点，准备监听消息
        for (Listener listener : listeners){
            //创建session
            Session session = null;
            try {
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                //创建消息接受的目的地
                //创建消息发送的地点
                Destination destination = null;
                if(Objects.equals(listener.messageDomain(),MessageEnum.P2P))
                    destination = new ActiveMQQueue(listener.destination());
                else
                    destination = new ActiveMQTopic(listener.destination());
                //创建消息消费者
                MessageConsumer consumer = session.createConsumer(destination);
                //消息监听
                consumer.setMessageListener(message -> {
                    BytesMessage bytesMessage = (BytesMessage) message;
                    try {
                        long bodyLength = bytesMessage.getBodyLength();
                        byte[] bs = new byte[Integer.valueOf(bodyLength+"")];
                        bytesMessage.readBytes(bs);
                        log.debug("监听到新的消息，消息长度为："+bs.length);
                        try {
                            Participant participant = objectSerializer.deSerialize(bs, Participant.class);
                            log.debug("转换为事务参与者对象（participant）成功，所属事务ID为："+participant.getTransactionId());
                            //从消息体中获取到事务参与者Participant后，开始准备通过反射调用方法
                            rmdtInvocationService.invoke(participant);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                });
            } catch (JMSException e) {
                e.printStackTrace();
            }

        }
    }
}
