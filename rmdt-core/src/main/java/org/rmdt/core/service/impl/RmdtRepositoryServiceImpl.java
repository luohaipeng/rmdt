package org.rmdt.core.service.impl;

import org.rmdt.common.config.RmdtConfig;
import org.rmdt.common.domain.Transaction;
import org.rmdt.core.bootstrap.ApplicationContextHolder;
import org.rmdt.core.repository.RmdtTransactionRepository;
import org.rmdt.core.service.RmdtApplicationService;
import org.rmdt.core.service.RmdtRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author luohaipeng
 */
@Service
public class RmdtRepositoryServiceImpl implements RmdtRepositoryService {

    @Autowired
    private RmdtApplicationService rmdtApplicationService;

    private RmdtTransactionRepository rmdtTransactionRepository;


    @Override
    public void start(RmdtConfig rmdtConfig) {

        rmdtTransactionRepository = ApplicationContextHolder.getBean(RmdtTransactionRepository.class.getName());
        ReposirotyModelSuffixIsNull(rmdtConfig);
        rmdtTransactionRepository.init(rmdtConfig);

    }


    /**
     * 如果用户没有配置存储模型名后缀，那么就获取应用名作为后缀
     * @param rmdtConfig
     */
    private void ReposirotyModelSuffixIsNull(RmdtConfig rmdtConfig) {
        if(Objects.isNull(rmdtConfig.getRepositoryConfig().getReposirotyModelSuffix())){
            rmdtConfig.getRepositoryConfig().setReposirotyModelSuffix(rmdtApplicationService.appName());
        }
    }

    @Override
    public void save(Transaction transaction) {
        rmdtTransactionRepository.inster(transaction);
    }


    @Override
    public void update(Transaction transaction) {
        rmdtTransactionRepository.update(transaction);
    }

    @Override
    public Transaction getById(String transactionId) {
        return rmdtTransactionRepository.getById(transactionId);
    }

    @Override
    public List<Transaction> findRecover(Date date,Integer retriedPeriod) {
        return rmdtTransactionRepository.findRecover(date,retriedPeriod);
    }
}
