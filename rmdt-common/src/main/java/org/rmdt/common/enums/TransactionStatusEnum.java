
package org.rmdt.common.enums;

import lombok.Getter;

/**
 * @author luohaipeng
 * 事务状态
 */
@Getter
public enum TransactionStatusEnum {

    ROLLBACK(0, "回滚"),
    BEGIN(1, "开始"),
    PRE_COMMIT(2, "预提交"),
    COMMIT(3, "提交"),
    FAILURE(4, "出错");
    //RECOVER(5, "重试");

    private Integer code;

    private String desc;

    TransactionStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
