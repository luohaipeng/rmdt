package org.rmdt.core.bootstrap;

import org.rmdt.common.config.RmdtConfig;
import org.rmdt.core.disruptor.RmdtTransactionEventProducer;
import org.rmdt.core.message.RmdtTransactionMessage;
import org.rmdt.core.message.impl.ActivemqTransactionMessage;
import org.rmdt.core.repository.RmdtTransactionRepository;
import org.rmdt.core.repository.impl.JdbcTransactionRepository;
import org.rmdt.core.serialize.ObjectSerializer;
import org.rmdt.core.service.RmdtInvocationService;
import org.rmdt.core.service.RmdtMQService;
import org.rmdt.core.service.RmdtRepositoryService;
import org.rmdt.core.service.RmdtSchedulerRecoverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

/**
 * @author luohaipeng
 * 初始化框架相关的信息支持类
 */
@Component
@Slf4j
public class RmdtBootstrapSupport {

    @Autowired
    private RmdtTransactionEventProducer rmdtTransactionEventProducer;//事务日志事件初始化和发布事件相关服务

    @Autowired
    private RmdtRepositoryService rmdtRepositoryService;//事务日志数据存储服务

    @Autowired
    private RmdtMQService rmdtMQService;//事务日志消息队列服务

    @Autowired
    private ObjectSerializer objectSerializer;//对象持久化工具

    @Autowired
    private RmdtInvocationService rmdtInvocationService;//方法反射调用服务

    @Autowired
    private RmdtSchedulerRecoverService rmdtSchedulerRecoverService;//错误事务回复定时任务服务

    /**
     * 接收配置对象，开始初始化框架相关的信息
     * @param rmdtConfig
     */
    public void initRmdt(RmdtConfig rmdtConfig) {
        log.debug("初始化Rmdt.......");
        //通过SPI的方式加载数据存储的支持，提高扩展性
        loadRepository(rmdtConfig);
        //通过SPI的方式加载MQ支持，提高扩展性
        loadMessageQueue(rmdtConfig);
        //启动事务日志事件处理程序
        rmdtTransactionEventProducer.start(rmdtConfig.getEventBufferSize());
        //启动事务日志存储相关支持
        rmdtRepositoryService.start(rmdtConfig);
        //启动事务日志可靠消息相关支持
        rmdtMQService.start(rmdtConfig);
        //定时检查错误的事务日志，重新发MQ消息调用,恢复事务一致性
        rmdtSchedulerRecoverService.schedulerTransactionRecover(rmdtConfig);
    }




    /**
     * 通过SPI的方式加载所有的数据存储服务提供者
     */
    private void loadRepository(RmdtConfig rmdtConfig) {

        //加载所有数据存储服务提供者
        ServiceLoader<RmdtTransactionRepository> load = ServiceLoader.load(RmdtTransactionRepository.class);
        //在所有提供者中查找用户配置的那个，如果找不到，则设置JDBC存储支持
        RmdtTransactionRepository repositorySupport = StreamSupport.stream(load.spliterator(), false)
                .filter(rmdtTransactionRepository -> Objects.equals(rmdtTransactionRepository.getRepositoryName(), rmdtConfig.getRepositoryName()))
                .findFirst()
                .orElse(new JdbcTransactionRepository());
        //给数据存储服务提供者设置一个对象序列化工具
        repositorySupport.setObjectSerializer(objectSerializer);
        //把数据存储服务提供者设置到spring容器中
        ApplicationContextHolder.setBean(RmdtTransactionRepository.class.getName(),repositorySupport);

    }

    /**
     * 通过SPI的方式加载所有的MQ服务提供者
     */
    private void loadMessageQueue(RmdtConfig rmdtConfig) {
        //加载所有消息队列服务提供者
        ServiceLoader<RmdtTransactionMessage> load = ServiceLoader.load(RmdtTransactionMessage.class);
        //在所有提供者中查找用户配置的那个，如果找不到，则设置ActiveMQ支持
        RmdtTransactionMessage messageQueue = StreamSupport.stream(load.spliterator(), false)
                .filter(mq -> Objects.equals(mq.getMessageQueueName(), rmdtConfig.getMessageQueueName()))
                .findFirst()
                .orElse(new ActivemqTransactionMessage());
        //给消息队列服务提供者设置一个对象序列化工具
        messageQueue.setObjectSerializer(objectSerializer);
        //给消息队列服务提供者设置一个方法反射调用的服务
        rmdtInvocationService.setRmdtConfig(rmdtConfig);
        messageQueue.setInvocationService(rmdtInvocationService);
        //把消息队列服务提供者设置到spring容器中
        ApplicationContextHolder.setBean(RmdtTransactionMessage.class.getName(),messageQueue);
    }

}
























