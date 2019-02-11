package org.rmdt.common.thread;


import org.rmdt.common.context.RmdtTransactionContext;
import org.rmdt.common.domain.RPCErrorInfo;
import org.rmdt.common.domain.Transaction;

/**
 * @author luohaipeng
 */
public class RmdtTransactionThreadLocal {

    //保存事务上下文对象
    public static final ThreadLocal<RmdtTransactionContext> CONTEXT_THREADLOCAL =  new ThreadLocal();

    //保存事务日志对象
    public static final ThreadLocal<Transaction> TRANSACTION_THREADLOCAL = new ThreadLocal();

    //保存RPC报错消息
    public static final ThreadLocal<RPCErrorInfo> RPC_ERROR_INFO_THREADLOCAL = new ThreadLocal();

    public static void clearThreadLocal() {
        CONTEXT_THREADLOCAL.remove();
        TRANSACTION_THREADLOCAL.remove();
        RPC_ERROR_INFO_THREADLOCAL.remove();
    }
}
