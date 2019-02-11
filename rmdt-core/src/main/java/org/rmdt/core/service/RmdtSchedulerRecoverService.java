package org.rmdt.core.service;

import org.rmdt.common.config.RmdtConfig;

/**
 * @author luohaipeng
 */
public interface RmdtSchedulerRecoverService {

    void schedulerTransactionRecover(RmdtConfig rmdtConfig);
}
