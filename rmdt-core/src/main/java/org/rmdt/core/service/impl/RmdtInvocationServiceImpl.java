package org.rmdt.core.service.impl;

import org.rmdt.common.config.RmdtConfig;
import org.rmdt.common.context.RmdtTransactionContext;
import org.rmdt.common.domain.Participant;
import org.rmdt.common.domain.Transaction;
import org.rmdt.common.enums.TransactionEventEnum;
import org.rmdt.common.enums.TransactionRoleEnum;
import org.rmdt.common.enums.TransactionStatusEnum;
import org.rmdt.common.thread.RmdtTransactionThreadLocal;
import org.rmdt.core.bootstrap.ApplicationContextHolder;
import org.rmdt.core.disruptor.RmdtTransactionEventProducer;
import org.rmdt.core.service.RmdtInvocationService;
import org.rmdt.core.service.RmdtRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author luohaipeng
 */
@Service
@Slf4j
public class RmdtInvocationServiceImpl implements RmdtInvocationService {

    @Autowired
    private RmdtRepositoryService repositoryService;
    @Autowired
    private RmdtTransactionEventProducer rmdtTransactionEventProducer;

    private RmdtConfig rmdtConfig;

    //private static Lock lock = new ReentrantLock();

    /**
     * 调用该方法的情况是队列消息消费监听到有新的消息
     * Participant对象就是消息体反序列化过来的
     * 一个Participant对象就代表一个事务参与者
     * 需要反射调用所使用的信息都包含在Participant对象中
     * <p>
     * <p>
     * 注意：该实现方法必须同步，因为消息队列可能存在多个消息，每监听到一个消息就会调用一次该方法，
     * 而该方法执行完需要时间，所以就会导致在极短的时间内，调用了多次该方法，造成数据幂等性出现问题，
     * 解决方法只需要加上同步，让一个线程执行完之后再进来下一个线程
     *
     * @param participant
     * @return
     */
    @Override
    public void invoke(Participant participant) {

        synchronized (RmdtInvocationServiceImpl.class) {
            //由于整个事务所有参与者事务id都是一样的，所以先通过事务id获取事务对象
            Transaction transaction = repositoryService.getById(participant.getTransactionId());
            RmdtTransactionThreadLocal.TRANSACTION_THREADLOCAL.set(transaction);
            //如果该事务对象不存在，说明RPC请求没有正常调用到远程方法，AOP不生效，没有在本地创建事务对象
            //这样的话，有可能是之前调用RPC时，服务器宕机了，重启了服务器后，监听到消息队列的消息，进入该方法，通过反射重新再调用一次，保证数据一致性

            if (Objects.isNull(transaction)) {
                //构建一条本地事务
                transaction = buildTransaction(participant);
                //如果transaction为null，需要重新给当前线程设置事务对象，否则不需要重新设置
                RmdtTransactionThreadLocal.TRANSACTION_THREADLOCAL.set(transaction);
                try {
                    //事务参与者开始通过反射调用方法
                    participantInvoke(participant);
                    //反射调用成功，本地事务日志状态设置为“提交”
                    transaction.setStatus(TransactionStatusEnum.COMMIT.getCode());
                } catch (Exception e) {
                    log.error(e.getMessage());
                    //反射调用失败，本地事务日志状态设置为“错误”，并设置错误信息
                    transaction.setStatus(TransactionStatusEnum.FAILURE.getCode());
                    transaction.setErrorMessage(e.getMessage());
                } finally {
                    //不管成功还是失败，这条新建的事务日志都需要插入到数据库
                    rmdtTransactionEventProducer.publish(transaction, TransactionEventEnum.INSERT.getCode());
                }
            }
            //否则，事务对象存在，说明RPC请求是正常调用到远程方法的，调用方法前通过AOP在本地创建过事务对象
            //所以，为了保证幂等性，之前调用过就不能再调用一次，不然就相当于一次RPC请求，调用了两次远程方法
            //但是需要判断事务是否有错，如果有错，就需要重新尝试反射调用方法
            else if (Objects.equals(transaction.getStatus(),TransactionStatusEnum.FAILURE.getCode())) {
                //先判断一下该错误事务日志重试的次数是否超过了配置的次数
                if (transaction.getRetriedCount() > rmdtConfig.getRetriedCount()) {
                    log.error(participant.getDestination() + "的服务："
                            + participant.getTargetClass().getName() + "." + participant.getMethodName()
                            + "对应的事务ID为：" + participant.getTransactionId() + "事务重试次数" + transaction.getRetriedCount()
                            + "超过了设置的重试次数，需要人工处理");
                    return;
                }
                try {
                    transaction.setRetriedCount(transaction.getRetriedCount() + 1);
                    //开始通过反射重试调用方法
                    participantInvoke(participant);
                    //反射调用成功，本地事务日志状态设置为“提交”
                    transaction.setStatus(TransactionStatusEnum.COMMIT.getCode());
                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {
                    //不管成功还是失败，这条事务日志都需要更新
                    rmdtTransactionEventProducer.publish(transaction, TransactionEventEnum.UPDATE.getCode());
                }
            }
        }
    }

    @Override
    public void setRmdtConfig(final RmdtConfig rmdtConfig) {
        this.rmdtConfig = rmdtConfig;
    }

    /**
     * @param participant
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void participantInvoke(Participant participant) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        //在通过反射调用方法前，先给当前线程设置事务上下文对象，事务ID为参与者participant的id，确保所有参与者都是同一个事务，角色为本地“LOCAL”
        //角色LOCAL是用于进入AOP时，不发起新的事务，不然的话就会多出一条事务日志
        RmdtTransactionContext rmdtTransactionContext = new RmdtTransactionContext();
        rmdtTransactionContext.setTransactionId(participant.getTransactionId());
        rmdtTransactionContext.setRole(TransactionRoleEnum.LOCAL.getCode());

        RmdtTransactionThreadLocal.CONTEXT_THREADLOCAL.set(rmdtTransactionContext);
        Class clazz = participant.getTargetClass();
        Method method = clazz.getMethod(participant.getMethodName(), participant.getParameterTypes());
        Object bean = ApplicationContextHolder.getBean(clazz);
        method.invoke(bean, participant.getArguments());
    }

    /**
     * 构建一条事务日志
     *
     * @param participant
     * @return
     */
    private Transaction buildTransaction(Participant participant) {
        Transaction transaction = new Transaction();
        transaction.setTargetMethod(participant.getMethodName());
        transaction.setTargetClass(participant.getTargetClass().getName());
        transaction.setRole(TransactionRoleEnum.ACTOR.getCode());
        transaction.setTransactionId(participant.getTransactionId());
        transaction.setStatus(TransactionStatusEnum.BEGIN.getCode());
        return transaction;
    }
}
