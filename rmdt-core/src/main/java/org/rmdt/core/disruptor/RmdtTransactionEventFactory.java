package org.rmdt.core.disruptor;


import com.lmax.disruptor.EventFactory;

/**
 * @author luohaipeng
 * 事务事件生产工厂
 */
public class RmdtTransactionEventFactory implements EventFactory<RmdtTransactionEvent> {
    @Override
    public RmdtTransactionEvent newInstance() {
        return new RmdtTransactionEvent();
    }
}
