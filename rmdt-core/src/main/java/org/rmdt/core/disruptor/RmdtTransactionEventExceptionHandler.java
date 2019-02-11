package org.rmdt.core.disruptor;

import com.lmax.disruptor.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luohaipeng
 * disruptor框架报错了会调用该对象对应的方法
 */
@Slf4j
public class RmdtTransactionEventExceptionHandler implements ExceptionHandler {

    /**
     * 事件消费者处理事件时报错
     * @param throwable
     * @param l
     * @param o
     */
    @Override
    public void handleEventException(Throwable throwable, long l, Object o) {
        log.error("处理事件报错："+throwable.getMessage());
    }

    @Override
    public void handleOnStartException(Throwable throwable) {
        log.error("启动disruptor报错："+throwable.getMessage());
    }

    @Override
    public void handleOnShutdownException(Throwable throwable) {
        log.error("停止disruptor报错："+throwable.getMessage());
    }
}
