package org.rmdt.demo.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter@Getter
public class InventoryVo implements Serializable {
    private Long productId;
    private Integer count;//购买数量
}
