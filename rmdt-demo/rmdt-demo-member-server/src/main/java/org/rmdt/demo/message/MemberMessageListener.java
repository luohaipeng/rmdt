package org.rmdt.demo.message;

import org.rmdt.annotation.Listener;
import org.springframework.stereotype.Component;

/**
 * 监听消息队列
 */
@Component
public class MemberMessageListener {


    @Listener(destination = "member-payment")
    public void listenerMemberPayment(){

    }

}
