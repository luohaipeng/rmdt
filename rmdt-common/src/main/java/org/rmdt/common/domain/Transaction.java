package org.rmdt.common.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author luohaipeng
 * 事务对象
 */
@Setter
@Getter
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    //事务id
    private String transactionId;
    //事务创建时间
    private Date createTime = new Date();
    //最后修改时间
    private Date lastTime = new Date();
    //事务角色
    private Integer role;
    //事务状态
    private Integer status;
    //调用的类的全限定名
    private String targetClass;
    //调用的方法名
    private String targetMethod;
    //错误信息
    private String errorMessage;
    //重试次数
    private Integer retriedCount = 0;
    //发送MQ消息的次数
    private Integer sendMessageCount = 0;
    //事务参与者集合
    private List<Participant> participants = new CopyOnWriteArrayList();
}
