package org.rmdt.core.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author luohaipeng
 * 切点拦截接口
 */
public interface RmdtTransactionInterceptor {

    /**
     * 对切入点的拦截操作
     */
    Object interceptor(ProceedingJoinPoint proceedingJoinPoint) throws Throwable;


}
