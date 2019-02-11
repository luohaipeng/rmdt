package org.rmdt.core.bootstrap;

import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author luohaipeng
 *负责保管spring应用环境上下文对象
 */
public class ApplicationContextHolder{

    private static ConfigurableApplicationContext applicationContext;

    public static void setApplicationContext(final ConfigurableApplicationContext applicationContext){
        ApplicationContextHolder.applicationContext = applicationContext;
    }

    public static <T>T getBean(String beanName){
        return (T) applicationContext.getBean(beanName);
    }

    public static <T>T getBean(Class<T> clazz){
        return (T) applicationContext.getBean(clazz);
    }

    public static void setBean(String beanName,Object obj){
        applicationContext.getBeanFactory().registerSingleton(beanName,obj);
    }

    public static ConfigurableApplicationContext getApplicationContext(){
        return applicationContext;
    }
}














