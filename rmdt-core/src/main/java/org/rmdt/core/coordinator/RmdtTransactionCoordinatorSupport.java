package org.rmdt.core.coordinator;

import org.rmdt.common.context.RmdtTransactionContext;
import org.rmdt.common.domain.Participant;
import org.rmdt.common.domain.RPCErrorInfo;
import org.rmdt.common.domain.Transaction;
import org.rmdt.common.enums.TransactionEventEnum;
import org.rmdt.common.enums.TransactionRoleEnum;
import org.rmdt.common.enums.TransactionStatusEnum;
import org.rmdt.common.thread.RmdtTransactionThreadLocal;
import org.rmdt.core.disruptor.RmdtTransactionEventProducer;
import org.rmdt.core.service.RmdtMQService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * @author luohaipeng
 * 提供具体事务控制处理支持
 */
@Component
@Slf4j
public class RmdtTransactionCoordinatorSupport {

    @Autowired
    private RmdtTransactionEventProducer rmdtTransactionEventProducer;

    @Autowired
    private RmdtMQService rmdtMQService;

    /**
     * 开启事务
     */
    public void begin(ProceedingJoinPoint point){
        //新建一个事务环境上下文对象
        RmdtTransactionContext rmdtTransactionContext = new RmdtTransactionContext();
        //把事务环境上下角色设置为“事务发起者”
        rmdtTransactionContext.setRole(TransactionRoleEnum.START.getCode());
        //构建一个事务日志对象
        Transaction transaction = buildTransaction(point,rmdtTransactionContext,TransactionStatusEnum.BEGIN.getCode());
        //发布插入事务日志对象的事件
        rmdtTransactionEventProducer.publish(transaction, TransactionEventEnum.INSERT.getCode());
        //把当前事务日志对象存储的到ThreadLocal中
        RmdtTransactionThreadLocal.TRANSACTION_THREADLOCAL.set(transaction);
        //把事务环境上下文对象存储的到ThreadLocal中
        RmdtTransactionThreadLocal.CONTEXT_THREADLOCAL.set(rmdtTransactionContext);
    }

    /**
     * 参与事务
     * @param point
     * @param rmdtTransactionContext
     * @return
     */
    public void participate(ProceedingJoinPoint point, RmdtTransactionContext rmdtTransactionContext) {
        //把从远程传过来的事务环境上下的角色设置修改为“事务参与者”
        rmdtTransactionContext.setRole(TransactionRoleEnum.ACTOR.getCode());
        //构建一个事务日志对象
        Transaction transaction = buildTransaction(point,rmdtTransactionContext,TransactionStatusEnum.BEGIN.getCode());
        //发布插入事务日志对象的事件
        rmdtTransactionEventProducer.publish(transaction, TransactionEventEnum.INSERT.getCode());
        //把当前事务日志对象存储的到ThreadLocal中
        RmdtTransactionThreadLocal.TRANSACTION_THREADLOCAL.set(transaction);
        //把事务环境上下文对象存储的到ThreadLocal中
        RmdtTransactionThreadLocal.CONTEXT_THREADLOCAL.set(rmdtTransactionContext);
    }

    /**
     * @param point
     * @param rmdtTransactionContext
     * @param status
     * @return
     */
    private Transaction buildTransaction(ProceedingJoinPoint point,RmdtTransactionContext rmdtTransactionContext,Integer status) {

        Class<?>[] interfaces = point.getTarget().getClass().getInterfaces();
        Class<?> clazz = Stream.of(interfaces).filter(i -> i.getSimpleName().contains("Service")).findFirst().get();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        Transaction transaction  = new Transaction();
        if(StringUtils.hasLength(rmdtTransactionContext.getTransactionId()))
            //如果是事务参与者的话，不需要新建事务ID，使用事务发起者的ID即可
            transaction.setTransactionId(rmdtTransactionContext.getTransactionId());
        else{
            //如果是事务发起者的话，需要新建事务ID，并设置到事务环境上下文对象中
            transaction.setTransactionId(String.valueOf(UUID.randomUUID().hashCode() & 0x7fffffff));
            rmdtTransactionContext.setTransactionId(transaction.getTransactionId());
        }
        transaction.setRole(rmdtTransactionContext.getRole());
        transaction.setStatus(status);
        transaction.setTargetClass(clazz.getName());
        transaction.setTargetMethod(method.getName());
        return transaction;
    }

    /**
     * 添加一个事务参与者
     * @param participant
     */
    public void addParticipant(Participant participant) {
        //把该事务参与者添加到当前事务中
        Transaction transaction = RmdtTransactionThreadLocal.TRANSACTION_THREADLOCAL.get();
        transaction.getParticipants().add(participant);
        //发布修改事务日志的事件
        rmdtTransactionEventProducer.publish(transaction,TransactionEventEnum.UPDATE.getCode());
    }

    /**
     * 提交事务
     */
    public void commit() {
        Transaction transaction = RmdtTransactionThreadLocal.TRANSACTION_THREADLOCAL.get();
        //在提交事务之前，先查看调用RPC接口时，是否有报错
        //如果有报错，则事务的状态为failure状态，否则才是commit
        RPCErrorInfo rpcErrorInfo = RmdtTransactionThreadLocal.RPC_ERROR_INFO_THREADLOCAL.get();
        if(Objects.nonNull(rpcErrorInfo) && Objects.equals(rpcErrorInfo.getCode(),1)){
            transaction.setStatus(TransactionStatusEnum.FAILURE.getCode());
            transaction.setErrorMessage(rpcErrorInfo.getErrorMsg());
        }else{
            transaction.setStatus(TransactionStatusEnum.COMMIT.getCode());
        }
        //发布修改事务日志的事件
        rmdtTransactionEventProducer.publish(transaction,TransactionEventEnum.UPDATE.getCode());
    }

    /**
     * 事务出错
     */
    public void fail(Throwable throwable) {
        Transaction transaction = RmdtTransactionThreadLocal.TRANSACTION_THREADLOCAL.get();
        transaction.setStatus(TransactionStatusEnum.FAILURE.getCode());
        transaction.setErrorMessage(throwable.getMessage());
        rmdtTransactionEventProducer.publish(transaction,TransactionEventEnum.UPDATE.getCode());
    }


    /**
     * 发送可靠消息
     */
    public void sendMessage() {
        Transaction transaction = RmdtTransactionThreadLocal.TRANSACTION_THREADLOCAL.get();
        transaction.getParticipants().forEach(participant -> {
            rmdtMQService.sendMessage(participant);
        });
    }


}



















