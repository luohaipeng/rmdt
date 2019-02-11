package org.rmdt.demo.service;

import org.rmdt.annotation.Rmdt;
import org.rmdt.demo.domain.OrderInfo;

import java.math.BigDecimal;

public interface OrderInfoService {


    /**
     * 创建订单
     * @param count 购买数量
     * @param price 每个商品价格
     * @return
     */
    OrderInfo createOrder(Integer count, BigDecimal price);

    /**
     * 订单支付
     * @param order
     */
    @Rmdt
    void makePayment(OrderInfo order);
}
