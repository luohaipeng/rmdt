package org.rmdt.core.service;

import org.rmdt.common.context.RmdtTransactionContext;
import org.rmdt.core.coordinator.RmdtTransactionCoordinator;

/**
 *@author luohaipeng
 */
public interface RmdtCoordinatorService {

    /**
     * 通过事务上下文对象各种状态，获取对应需要使用的事务协调者
     * @param remoteTransactionContext
     * @return
     */
    RmdtTransactionCoordinator getHandlerStrategy(RmdtTransactionContext remoteTransactionContext);
}
