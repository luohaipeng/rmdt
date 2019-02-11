package org.rmdt.dubbo.interceptor;

import org.rmdt.common.constant.RmdtConstant;
import org.rmdt.common.context.RmdtTransactionContext;
import org.rmdt.core.coordinator.RmdtTransactionCoordinator;
import org.rmdt.core.interceptor.RmdtTransactionInterceptor;
import org.rmdt.core.service.RmdtCoordinatorService;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


/**
 * @author luohaipeng
 * dubbo框架切入点拦截类
 */
@Component
@Slf4j
public class DubboRmdtTransactionInterceptor implements RmdtTransactionInterceptor {

    //提供事务处理的Handler
    private RmdtTransactionCoordinator rmdtTransactionCoordinator;

    @Autowired
    private RmdtCoordinatorService rmdtCoordinatorService;

    /**
     * 切入点拦截处理
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Override
    public Object interceptor(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        //获取dubbo的RpcContext对象，并获取该对象中的rmdtTransactionContext参数对应的值，该值是从远程传过来的，数据的格式为json
        String remotoTransactionContextValue = RpcContext.getContext().getAttachment(RmdtConstant.RMDT_TRANSACTION_CONTEXT);

        //远程传入的事务环境上下文对象
        RmdtTransactionContext remotoTransactionContext = null;

        //如果rmdtTransactionContextValue不为null,则转为RmdtTransactionContext对象
        if(StringUtils.hasLength(remotoTransactionContextValue))
            remotoTransactionContext = JSON.parseObject(remotoTransactionContextValue, RmdtTransactionContext.class);
        //获取事务处理的Handler策略（也就是RmdtTransactionHandler的具体实现类）
        rmdtTransactionCoordinator = rmdtCoordinatorService.getHandlerStrategy(remotoTransactionContext);
        log.debug("进入AOP，获取到从远程传过来的RPC参数remotoTransactionContextValue为："+remotoTransactionContextValue+"当前本地事务角色为："+ rmdtTransactionCoordinator.handlerRoleType().getDesc());
        Object procee = rmdtTransactionCoordinator.handler(proceedingJoinPoint, remotoTransactionContext);
        return procee;
    }
}
