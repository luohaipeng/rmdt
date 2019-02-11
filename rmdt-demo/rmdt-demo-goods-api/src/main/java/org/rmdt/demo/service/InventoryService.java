package org.rmdt.demo.service;

import org.rmdt.annotation.Rmdt;
import org.rmdt.demo.domain.Inventory;
import org.rmdt.demo.vo.InventoryVo;

public interface InventoryService {


    @Rmdt(destination = "goods-decrease")
    boolean decrease(InventoryVo inventoryVo);

    Inventory getByProductId(Long productId);
}
