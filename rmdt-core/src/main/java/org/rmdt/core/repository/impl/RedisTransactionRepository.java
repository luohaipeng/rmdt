package org.rmdt.core.repository.impl;

import org.rmdt.common.config.RmdtConfig;
import org.rmdt.common.domain.Transaction;
import org.rmdt.core.repository.RmdtTransactionRepository;
import org.rmdt.core.serialize.ObjectSerializer;

import java.util.Date;
import java.util.List;

public class RedisTransactionRepository implements RmdtTransactionRepository {

    @Override
    public void setObjectSerializer(ObjectSerializer objectSerializer) {

    }

    @Override
    public String getRepositoryName() {
        return "redis";
    }

    @Override
    public void init(RmdtConfig rmdtConfig) {
        System.out.println();
    }

    @Override
    public Integer inster(Transaction transaction) {
        return 0;
    }

    @Override
    public Integer update(Transaction transaction) {
        return 0;
    }

    @Override
    public Transaction getById(String transactionId) {
        return null;
    }

    @Override
    public List<Transaction> findRecover(Date date,Integer retriedPeriod) {
        return null;
    }


}
