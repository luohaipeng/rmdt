package org.rmdt.core.coordinator;


import org.rmdt.common.context.RmdtTransactionContext;
import org.rmdt.common.enums.TransactionRoleEnum;
import org.rmdt.common.thread.RmdtTransactionThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author luohaipeng
 * 提供事务发起者角色相关处理的事务协调者
 */
@Slf4j
@Component("StartTransactionCoordinator")
public class StartTransactionCoordinator implements RmdtTransactionCoordinator {

    @Autowired
    private RmdtTransactionCoordinatorSupport rmdtTransactionCoordinatorSupport;

    public Object handler(ProceedingJoinPoint proceedingJoinPoint,RmdtTransactionContext remoteTransactionContext) throws Throwable {
        try {
            rmdtTransactionCoordinatorSupport.begin(proceedingJoinPoint);
            log.debug("开启事务。。。。。。。。。。。。。。");
            //执行切入点的方法，在方法中，对调用的远程开启了分布式服务的方法进行过滤拦截，给当前事务添加事务参与者
            Object proceed = proceedingJoinPoint.proceed();
            //提交事务
            rmdtTransactionCoordinatorSupport.commit();
            log.debug("提交事务。。。。。。。。。。。。。");
            return proceed;
        }catch (Throwable throwable){
            //在AOP切入点或者是切入点前后抛出错误，则把当前事务设置为出错
            rmdtTransactionCoordinatorSupport.fail(throwable);
            log.debug("事务出错。。。。。。。。。。。。。");
            throw throwable;
        }finally {
            //不管是否出错，最终还是需要发送MQ消息
            log.debug("给当前事务的参与者发送MQ消息。。。。。。。。。。。。。");
            rmdtTransactionCoordinatorSupport.sendMessage();
            RmdtTransactionThreadLocal.clearThreadLocal();
        }
    }

    @Override
    public TransactionRoleEnum handlerRoleType() {
        return TransactionRoleEnum.START;
    }
}







