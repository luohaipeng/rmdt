package org.rmdt.common.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author luohaipeng
 * 事务参与者
 */
@Setter@Getter
public class Participant implements Serializable {

    private static final long serialVersionUID = 1L;
    //事务id
    private String transactionId;
    //发送可靠消息所使用的消息传递域（如：点对点、发布/订阅方式）
    private Integer messageDomain;
    //发送可靠消息的地点
    private String destination;
    //事务参与者所在的类
    private Class targetClass;
    //事务参与者所调用的方法名
    private String methodName;
    //事务参与者所调用的方法参数类型
    private Class[] parameterTypes;
    //事务参与者所调用的方法参数值
    private Object[] arguments;
}
