package org.rmdt.annotation;


import org.rmdt.common.enums.MessageEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author luohaipeng
 * 在需要处理分布式事务的service接口上贴上该注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Rmdt {

    /**
     * 给远程事务参与者发送可靠消息的地点
     * @return
     */
    String destination() default "";

    /**
     * 消息传递域的类型
     * @return
     */
    MessageEnum messageDomain() default MessageEnum.P2P;
}
