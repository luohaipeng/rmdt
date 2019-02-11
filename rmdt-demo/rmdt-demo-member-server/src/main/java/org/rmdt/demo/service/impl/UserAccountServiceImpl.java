package org.rmdt.demo.service.impl;

import org.rmdt.annotation.Rmdt;
import org.rmdt.demo.domain.UserAccount;
import org.rmdt.demo.mapper.UserAccountMapper;
import org.rmdt.demo.service.UserAccountService;
import org.rmdt.demo.vo.UserAccountVo;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Rmdt(destination="member-payment")
    public boolean payment(UserAccountVo userAccountVo) {
        UserAccount userAccount = userAccountMapper.getByUserId(userAccountVo.getUserId());
        if(userAccount.getBalance().compareTo(userAccountVo.getTotalAmount()) <= 0){
            throw new RuntimeException("资金不足！");
        }
        //int i = 1/0;
        userAccount.setBalance(userAccount.getBalance().subtract(userAccountVo.getTotalAmount()));
        userAccount.setUpdateTime(new Date());
        userAccountMapper.updateByUserId(userAccount);
        return Boolean.TRUE;
    }

    @Override
    public UserAccount getByUserId(Long userId) {
        return userAccountMapper.getByUserId(userId);
    }
}
