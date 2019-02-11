package org.rmdt.demo.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter@Getter
public class Inventory implements Serializable {
    private Long id;

    private Long productId;

    private Integer totalInventory;


    private Date createTime;

    private Date updateTime;
}