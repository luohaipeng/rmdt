package org.rmdt.core.disruptor;

import org.rmdt.common.enums.TransactionEventEnum;
import org.rmdt.core.service.RmdtRepositoryService;
import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author luohaipeng
 * 事件消费者（也就是具体做事件处理的对象）
 */
@Slf4j
public class RmdtTransactionEventConsumer implements WorkHandler<RmdtTransactionEvent> {

    private RmdtRepositoryService rmdtRepositoryService;

    public RmdtTransactionEventConsumer(RmdtRepositoryService rmdtRepositoryService){
        this.rmdtRepositoryService = rmdtRepositoryService;
    }
    /**
     * 当有新的事件发布出来的时候disruptor框架会调用该方法
     * @param rmdtTransactionEvent  事件对象
     * @throws Exception
     */
    @Override
    public void onEvent(RmdtTransactionEvent rmdtTransactionEvent) throws Exception {
        if(Objects.equals(rmdtTransactionEvent.getTransactionEventType(),TransactionEventEnum.INSERT.getCode())){
            rmdtRepositoryService.save(rmdtTransactionEvent.getTransaction());
        }else if(Objects.equals(rmdtTransactionEvent.getTransactionEventType() ,TransactionEventEnum.UPDATE.getCode())){
            rmdtRepositoryService.update(rmdtTransactionEvent.getTransaction());
        }
    }
}
