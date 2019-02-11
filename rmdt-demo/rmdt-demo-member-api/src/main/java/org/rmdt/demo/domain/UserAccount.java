package org.rmdt.demo.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter@Getter
public class UserAccount implements Serializable {
    private Long id;

    private Long userId;

    private BigDecimal balance;

    private Date createTime;

    private Date updateTime;


}