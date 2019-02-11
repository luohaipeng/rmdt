package org.rmdt.common.config;

import org.rmdt.common.config.message.BaseMessageConfig;
import org.rmdt.common.config.repository.BaseRepositoryConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * @author luohaipeng
 * Rmdt框架封装配置信息的对象
 */
@Setter@Getter
public class RmdtConfig {

    private Integer eventBufferSize = 2048; //事务日志消息事件缓存数，必须为2的N次方，默认为2048

    private Integer retriedDelayTime = 60; //出错事务重试调用的滞后时间（单位是秒）

    private Integer retriedPeriod = 120; //出错事务重试调用的周期时间（单位是秒）

    private Integer retriedCount = 4; //出错事务重试调用的最大次数

    private String repositoryName = "jdbc"; //事务日志数据存储支持，默认为jdbc

    private BaseRepositoryConfig repositoryConfig; //事务日志存储技术支持相关配置信息（如连接地址，账号密码等等）

    private String messageQueueName = "activemq"; //使用消息队列发送可靠消息，默认为ActiveMQ

    private BaseMessageConfig messageConfig;


}
