package org.rmdt.core.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author luohaipeng
 * aop切面（这里考虑到后续RPC框架扩展，所以该类定义为抽象类，用于各种RPC框架继承并创建实例）
 */
@Aspect
public abstract class AbstractRmdtTransactionAspect {


    /**
     * 切点拦截接口
     */
    private RmdtTransactionInterceptor rmdtTransactionInterceptor;

    /**
     * 注入具体的切点拦截类
     * @param rmdtTransactionInterceptor
     */
    public void setRmdtTransactonInterceptor(RmdtTransactionInterceptor rmdtTransactionInterceptor){
        this.rmdtTransactionInterceptor = rmdtTransactionInterceptor;
    }

    /**
     * 定义切入点（在方法上贴有Rmdt注解的都进入切点）
     */
    @Pointcut("@annotation(org.rmdt.annotation.Rmdt)")
    public void rmdtTransactionPointcut(){}


    /**
     * 切入方式为环绕通知
     */
    @Around("rmdtTransactionPointcut()")
    public Object rmdtAnnotationMethodAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object proceed = rmdtTransactionInterceptor.interceptor(proceedingJoinPoint);
        return proceed;
    }

}
