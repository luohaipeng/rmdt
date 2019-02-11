package org.rmdt.demo.controller;

import org.rmdt.demo.domain.OrderInfo;
import org.rmdt.demo.service.OrderInfoService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/api/orders")
public class OrderController {

    @Reference
    private OrderInfoService orderInfoService;

    @PostMapping(value = "")
    public String createOrder(Integer count, BigDecimal price){
        OrderInfo order = orderInfoService.createOrder(count, price);
        orderInfoService.makePayment(order);
        return "success";
    }
}
