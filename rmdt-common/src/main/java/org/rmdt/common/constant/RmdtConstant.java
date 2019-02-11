package org.rmdt.common.constant;

/**
 * @author luohaipeng
 * 分布式事务框架公共常量
 */
public class RmdtConstant {

    /**
     * 事务环境上下文参数名（用户远程方法调用，用户远程方法调用附带该参数）
     */
    public final static String RMDT_TRANSACTION_CONTEXT = "rmdtTransactionContext";

    /**
     * 根据CPU核心数得出最合理的线程数量
     */
    public final static Integer THREAD_NUMBER = Runtime.getRuntime().availableProcessors() << 1;

    /**
     * 分布式事务框架名称
     */
    public final static String FRAMEWORK_NAME = "rmdt";

    /**
     * jdbc执行错误
     */
    public final static Integer JDBC_ERROR = 0;
}
