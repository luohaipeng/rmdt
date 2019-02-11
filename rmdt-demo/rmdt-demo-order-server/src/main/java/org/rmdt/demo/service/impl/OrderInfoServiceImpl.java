package org.rmdt.demo.service.impl;

import org.rmdt.annotation.Rmdt;
import org.rmdt.demo.domain.OrderInfo;
import org.rmdt.demo.enums.OrderStatusEnum;
import org.rmdt.demo.mapper.OrderInfoMapper;
import org.rmdt.demo.service.InventoryService;
import org.rmdt.demo.service.OrderInfoService;
import org.rmdt.demo.service.UserAccountService;
import org.rmdt.demo.vo.InventoryVo;
import org.rmdt.demo.vo.UserAccountVo;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Slf4j
public class OrderInfoServiceImpl implements OrderInfoService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Reference(proxy = "rmdtProxyFactory")
    private UserAccountService userAccountService;
    @Reference(proxy = "rmdtProxyFactory")
    private InventoryService inventoryService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderInfo createOrder(Integer count, BigDecimal price) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCount(count);
        orderInfo.setCreateTime(new Date());
        //inventory表中的商品id
        orderInfo.setProductId(1L);
        //userAccount表中的用户id
        orderInfo.setUserId(1L);
        orderInfo.setTotalAmount(price.multiply(new BigDecimal(count)));
        orderInfo.setStatus(OrderStatusEnum.NOT_PAY.getCode());
        orderInfoMapper.insert(orderInfo);
        return orderInfo;
    }

    @Override
    @Rmdt
    @Transactional(rollbackFor = Exception.class)
    public void makePayment(OrderInfo order) {

       /* UserAccount userAccount = userAccountService.getByUserId(order.getUserId());
        if (userAccount.getBalance().compareTo(order.getTotalAmount()) < 0) {
            log.error("余额不足。。");
            return;
        }
        Inventory inventory = inventoryService.getByProductId(order.getProductId());
        if (inventory.getTotalInventory() < order.getCount()) {
            log.error("库存不足。。");
            return;
        }*/
        //修改订单状态为成功
        order.setStatus(OrderStatusEnum.PAY_SUCCESS.getCode());
        orderInfoMapper.updateByPrimaryKey(order);
        //扣除用户余额
        UserAccountVo userAccountVo = new UserAccountVo();
        userAccountVo.setTotalAmount(order.getTotalAmount());
        userAccountVo.setUserId(order.getUserId());
        userAccountService.payment(userAccountVo);
        //int i = 1/0;
        //进入扣减库存操作
        InventoryVo inventoryVo = new InventoryVo();
        inventoryVo.setCount(order.getCount());
        inventoryVo.setProductId(order.getProductId());
        inventoryService.decrease(inventoryVo);

    }
}
