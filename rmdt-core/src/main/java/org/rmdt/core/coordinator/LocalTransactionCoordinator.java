package org.rmdt.core.coordinator;


import org.rmdt.common.context.RmdtTransactionContext;
import org.rmdt.common.enums.TransactionRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

/**
 * @author luohaipeng
 * 提供本地事务角色相关处理的事务协调者
 */
@Slf4j
@Component("LocalTransactionCoordinator")
public class LocalTransactionCoordinator implements RmdtTransactionCoordinator {


    /**
     * 本地事务直接放行切点，不做分布式事务日志处理，不然就会找出事务日志数据不一致
     * @param proceedingJoinPoint  AOP切点
     * @param remoteTransactionContext  远程传入的事务环境上下文对象
     * @return
     * @throws Throwable
     */
    public Object handler(ProceedingJoinPoint proceedingJoinPoint,RmdtTransactionContext remoteTransactionContext) throws Throwable {
        return proceedingJoinPoint.proceed();
    }

    @Override
    public TransactionRoleEnum handlerRoleType() {
        return TransactionRoleEnum.LOCAL;
    }
}







