
package org.rmdt.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author luohaipeng
 * 事务状态
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum TransactionStatusEnum {

    ROLLBACK(0, "回滚"),
    BEGIN(1, "开始"),
    PRE_COMMIT(2, "预提交"),
    COMMIT(3, "提交"),
    FAILURE(4, "出错");
    //RECOVER(5, "重试");

    private Integer code;

    private String desc;



}
