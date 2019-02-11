package org.rmdt.demo.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum{

    NOT_PAY(new Byte("1"),"未支付"),
    PAYING(new Byte("2"), "支付中"),
    PAY_FAIL(new Byte("3"), "支付失败"),
    PAY_SUCCESS(new Byte("4"), "支付成功");
    private Byte code;
    private String desc;
    OrderStatusEnum(Byte code,String desc){
        this.code = code;
        this.desc = desc;
    }
}
