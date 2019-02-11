
package org.rmdt.common.enums;

import lombok.Getter;

/**
 * @author luohaipeng
 * 事务角色
 */
@Getter
public enum TransactionRoleEnum {



    START(1, "事务发起者"),

    ACTOR(2, "事务参与者"),

    LOCAL(3, "本地事务");

    private Integer code;

    private String desc;

    TransactionRoleEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
