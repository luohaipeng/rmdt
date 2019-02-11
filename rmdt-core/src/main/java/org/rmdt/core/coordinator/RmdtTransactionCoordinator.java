package org.rmdt.core.coordinator;


import org.rmdt.common.context.RmdtTransactionContext;
import org.rmdt.common.enums.TransactionRoleEnum;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author luohaipeng
 * 事务协调者（AOP调用链最底层处理）
 * 使用策略模式，每个具体的实现类代表一种事务处理策略
 */
public interface RmdtTransactionCoordinator {

    /**
     * @param proceedingJoinPoint  AOP切点
     * @param remoteTransactionContext  远程传入的事务环境上下文对象
     * @return
     * @throws Throwable
     */
    Object handler(ProceedingJoinPoint proceedingJoinPoint, RmdtTransactionContext remoteTransactionContext) throws Throwable;


    /**
     * 实现类处理的角色类型
     * @return
     */
    TransactionRoleEnum handlerRoleType();
}
