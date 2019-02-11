package org.rmdt.demo.message;

import org.rmdt.annotation.Listener;
import org.springframework.stereotype.Component;

/**
 * 监听消息队列
 */
@Component
public class GoodsMessageListener {


    @Listener(destination = "goods-decrease")
    public void listenerMemberPayment(){

    }

}
