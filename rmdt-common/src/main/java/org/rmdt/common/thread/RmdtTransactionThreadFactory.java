package org.rmdt.common.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author luohaipeng
 * 自定义线程工厂
 */

public class RmdtTransactionThreadFactory implements ThreadFactory {

    /**
     * 统计创建线程数
     */
    private AtomicInteger threadCount = new AtomicInteger(1);

    private String threadNamePrefix;

    public RmdtTransactionThreadFactory(String threadNamePrefix){
        this.threadNamePrefix = threadNamePrefix;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        return new Thread(null,runnable, threadNamePrefix + threadCount.getAndIncrement());
    }
}
