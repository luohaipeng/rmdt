package org.rmdt.core.bootstrap;

import org.rmdt.common.config.RmdtConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author luohaipeng
 * Rmdt分布式事务框架启动引导，在spring容器初始化后会调用setApplicationContext方法
 * 该对象由用户创建并配置RmdtConfig中启动所需的相关信息
 */
public class RmdtBootstrap implements ApplicationContextAware {

    @Setter@Getter
    private RmdtConfig rmdtConfig;

    @Autowired
    private RmdtBootstrapSupport rmdtBootstrapSupport;
    /**
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        //把spring的应用环境上下文对象保存起来
        ApplicationContextHolder.setApplicationContext((ConfigurableApplicationContext) applicationContext);
        //初始化Rmdt配置
        initRmdt(this.rmdtConfig);
    }

    /**
     * 接收配置对象，开始初始化框架相关的信息
     * @param rmdtConfig
     */
    private void initRmdt(RmdtConfig rmdtConfig) {
        rmdtBootstrapSupport.initRmdt(rmdtConfig);
    }

}
