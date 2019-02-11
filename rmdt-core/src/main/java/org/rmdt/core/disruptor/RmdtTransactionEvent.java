package org.rmdt.core.disruptor;

import org.rmdt.common.domain.Transaction;
import lombok.Getter;
import lombok.Setter;

/**
 * @author luohaipeng
 * 事务事件对象
 */
@Setter@Getter
public class RmdtTransactionEvent {

    //操作事务的事件类型
    private Integer transactionEventType;
    //操作的事务对象
    private Transaction transaction;
}
