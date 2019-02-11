package org.rmdt.demo.config;

import org.rmdt.common.config.RmdtConfig;
import org.rmdt.common.config.message.ActivemqMessageConfig;
import org.rmdt.common.config.repository.JdbcRepositoryConfig;
import org.rmdt.core.bootstrap.RmdtBootstrap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoodsServerConfig {

    @Bean
    public RmdtBootstrap rmdtBootstrap(){
        RmdtBootstrap rmdtBootstrap = new RmdtBootstrap();
        RmdtConfig rmdtConfig = new RmdtConfig();

        //选择使用jdbc存储技术
        rmdtConfig.setRepositoryName("jdbc");
        JdbcRepositoryConfig jdbcConfig = new JdbcRepositoryConfig();
        //事务日志数据存储模型名
        jdbcConfig.setReposirotyModelSuffix("goods-server");
        jdbcConfig.setUrl("jdbc:mysql://localhost:3306/rmdt");
        jdbcConfig.setUsername("root");
        jdbcConfig.setPassword("root");
        //把jdbc配置类设置到RmdtConfig类中
        rmdtConfig.setRepositoryConfig(jdbcConfig);

        //选择使用ActiveMQ消息队列
        rmdtConfig.setMessageQueueName("activemq");
        ActivemqMessageConfig activemqConfig = new ActivemqMessageConfig();
        activemqConfig.setUrl("tcp://127.0.0.1:61616");
        activemqConfig.setUserName("admin");
        activemqConfig.setPassword("admin");
        //把ActiveMQ配置类设置到RmdtConfig类中
        rmdtConfig.setMessageConfig(activemqConfig);

        //把RmdtConfig配置类设置到启动引导类RmdtBootstrap中
        rmdtBootstrap.setRmdtConfig(rmdtConfig);
        return rmdtBootstrap;
    }
}
