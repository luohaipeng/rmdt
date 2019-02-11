package org.rmdt.demo.service.impl;

import org.rmdt.annotation.Rmdt;
import org.rmdt.demo.domain.Inventory;
import org.rmdt.demo.mapper.InventoryMapper;
import org.rmdt.demo.service.InventoryService;
import org.rmdt.demo.vo.InventoryVo;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryMapper inventoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Rmdt(destination = "goods-decrease")
    public boolean decrease(InventoryVo inventoryVo) {
        Inventory inventory = inventoryMapper.getByProductId(inventoryVo.getProductId());
        if(inventory.getTotalInventory() < inventoryVo.getCount()){
            throw new RuntimeException("库存不足");
        }
        //int i = 1/0;
        inventory.setTotalInventory(inventory.getTotalInventory() - inventoryVo.getCount());
        inventory.setUpdateTime(new Date());
        inventoryMapper.updateByProductId(inventory);
        return Boolean.TRUE;
    }

    @Override
    public Inventory getByProductId(Long productId) {
        return inventoryMapper.getByProductId(productId);
    }
}
