
package org.rmdt.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author luohaipeng
 * 事务角色
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum TransactionRoleEnum {



    START(1, "事务发起者"),

    ACTOR(2, "事务参与者"),

    LOCAL(3, "本地事务");

    private Integer code;

    private String desc;



}
