package org.rmdt.core.disruptor;

import org.rmdt.common.domain.Transaction;
import com.lmax.disruptor.EventTranslatorTwoArg;

/**
 * @author luohaipeng
 * 事件对象转换器
 * 实际上，disruptor.publishEvent方法底层是调用了：
 * RingBuffer<RmdtTransactionEvent> ringBuffer = disruptor.getRingBuffer();
 * long sequence = ringBuffer.next();
 * RmdtTransactionEvent rmdtTransactionEvent = ringBuffer.get(sequence);
 * 得到RmdtTransactionEvent对象，然后通过调用translateTo方法封装参数，完成事件的发布
 */
public class RmdtTransactionEventTranslator implements EventTranslatorTwoArg<RmdtTransactionEvent,Transaction,Integer> {


    @Override
    public void translateTo(RmdtTransactionEvent rmdtTransactionEvent, long l, Transaction transaction, Integer transactionEventType) {
        rmdtTransactionEvent.setTransaction(transaction);
        rmdtTransactionEvent.setTransactionEventType(transactionEventType);
    }
}
