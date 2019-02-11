package org.rmdt.common.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author luohaipeng
 * 当前事务环境上下文bean对象
 * 用于在远程方法之间传递信息
 */
@Getter@Setter@NoArgsConstructor@AllArgsConstructor
public class RmdtTransactionContext implements Serializable {

    //当前事务日志id
    private String transactionId;
    //当前事务角色
    private Integer role;
}
