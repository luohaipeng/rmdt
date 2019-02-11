package org.rmdt.demo.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
@Setter@Getter
public class OrderInfo implements Serializable {
    private Long id;

    private Long userId;

    private Long productId;

    private BigDecimal totalAmount;

    private Integer count;

    private Byte status;

    private Date createTime;


}