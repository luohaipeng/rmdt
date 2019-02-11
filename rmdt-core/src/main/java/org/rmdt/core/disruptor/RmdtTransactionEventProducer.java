package org.rmdt.core.disruptor;

import org.rmdt.common.constant.RmdtConstant;
import org.rmdt.common.domain.Transaction;
import org.rmdt.common.thread.RmdtTransactionThreadFactory;
import org.rmdt.core.service.RmdtRepositoryService;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author luohaipeng
 * 事务事件生产者
 */
@Component
public class RmdtTransactionEventProducer {

    @Autowired
    private RmdtRepositoryService rmdtRepositoryService;
    private Disruptor disruptor;
    /**
     * 启动事务事件处理程序
     */
    public void start(int eventBufferSize){
        // 构造Disruptor对象，构造方法有5个参数。
        // 参数1为事务事件工厂，用户给Disruptor框架创建事务事件对象
        // 参数2为需要创建多少个事务事件对象,数量必须为2的N次方
        // 参数3为ThreadFaction对象,该对象是到时候创建事件消费者时要使用的，一个事件消费者对应一个线程
        // 参数4为事件生产者类型
        // 参数5为等待策略
        disruptor = new Disruptor<>(new RmdtTransactionEventFactory(), eventBufferSize, new RmdtTransactionThreadFactory("disruptor-thread-"), ProducerType.MULTI, new BlockingWaitStrategy());
        //存放事件消费者的数组，数组长度为最佳线程数
        RmdtTransactionEventConsumer[] rmdtTransactionEventConsumers = new RmdtTransactionEventConsumer[RmdtConstant.THREAD_NUMBER];
        //初始化件消费者数组
        for(int i=0;i<RmdtConstant.THREAD_NUMBER;i++){
            rmdtTransactionEventConsumers[i] = new RmdtTransactionEventConsumer(rmdtRepositoryService);
        }
        //把事件消费者的数组作为disruptor的工作池，但时候就由池中的这些消费者处理事件
        disruptor.handleEventsWithWorkerPool(rmdtTransactionEventConsumers);
        //为disruptor设置错误处理器
        disruptor.setDefaultExceptionHandler(new RmdtTransactionEventExceptionHandler());
        //启动disruptor
        disruptor.start();

    }

    /**
     * 关闭事务事件处理程序
     */
    public void shutdown(){
        disruptor.shutdown();
    }

    /**
     * 发布事务事件（RmdtTransactionEventTranslator对象会把transaction和transactionEventType两个参数转成TransactionEvent对象）
     * @param transaction  事务对象
     * @param transactionEventType  事务事件类型
     */
    public void publish(Transaction transaction,Integer transactionEventType){
        disruptor.publishEvent(new RmdtTransactionEventTranslator(),transaction,transactionEventType);
    }

    /*public static void main(String[] args) {
        int eventBufferSize = 2048;

        Disruptor disruptor = new Disruptor<>(new RmdtTransactionEventFactory(), eventBufferSize, new RmdtTransactionThreadFactory(), ProducerType.MULTI, new BlockingWaitStrategy());
        RmdtTransactionEventConsumer[] rmdtTransactionEventConsumers = new RmdtTransactionEventConsumer[RmdtConstant.THREAD_NUMBER];
        for(int i=0;i<RmdtConstant.THREAD_NUMBER;i++){
            rmdtTransactionEventConsumers[i] = new RmdtTransactionEventConsumer();
        }
        disruptor.handleEventsWithWorkerPool(rmdtTransactionEventConsumers);
        disruptor.start();
        for(int i=0;i<2000;i++){

            //disruptor.publishEvent(new RmdtTransactionEventTranslator(),"测试disruptor");
        }
    }*/
}
