package org.rmdt.demo.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter@Getter
public class UserAccountVo implements Serializable {
    private Long userId;
    private BigDecimal totalAmount;//扣款总额
}
