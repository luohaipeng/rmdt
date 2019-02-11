package org.rmdt.demo.mapper;

import org.rmdt.demo.domain.Inventory;

import java.util.List;

public interface InventoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Inventory record);

    Inventory selectByPrimaryKey(Long id);

    List<Inventory> selectAll();

    int updateByPrimaryKey(Inventory record);

    Inventory getByProductId(Long productId);

    void updateByProductId(Inventory inventory);
}