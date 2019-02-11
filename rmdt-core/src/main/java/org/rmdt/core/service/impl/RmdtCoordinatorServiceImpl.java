package org.rmdt.core.service.impl;

import org.rmdt.common.context.RmdtTransactionContext;
import org.rmdt.common.enums.TransactionRoleEnum;
import org.rmdt.common.thread.RmdtTransactionThreadLocal;
import org.rmdt.core.bootstrap.ApplicationContextHolder;
import org.rmdt.core.coordinator.RmdtTransactionCoordinator;
import org.rmdt.core.service.RmdtCoordinatorService;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author luohaipeng
 */
@Service
public class RmdtCoordinatorServiceImpl implements RmdtCoordinatorService {
    @Override
    public RmdtTransactionCoordinator getHandlerStrategy(RmdtTransactionContext remoteTransactionContext) {
        //如果远程传入的事务上下文对象为null，则当前事务有可能为事务发起者，或者本地事务
        if(Objects.isNull(remoteTransactionContext)){
            //获取当前线程的事务上下文对象，如果不为空，并且角色为local，则当前事务为本地事务
            //通过MQ调用的情况，事务上下文对象的角色是设置为Local的
            RmdtTransactionContext localTransactionContext = RmdtTransactionThreadLocal.CONTEXT_THREADLOCAL.get();
            if(Objects.nonNull(localTransactionContext) && Objects.equals(localTransactionContext.getRole(),TransactionRoleEnum.LOCAL.getCode())){
                return ApplicationContextHolder.getBean("LocalTransactionCoordinator");
            }
            //否则为事务发起者
            else{
                return ApplicationContextHolder.getBean("StartTransactionCoordinator");
            }
        }
        //否则为事务参与者的情况
        else{
            return ApplicationContextHolder.getBean("ActorTransactionCoordinator");
        }

    }
}
