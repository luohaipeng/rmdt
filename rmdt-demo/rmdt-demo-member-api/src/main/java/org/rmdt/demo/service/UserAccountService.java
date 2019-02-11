package org.rmdt.demo.service;

import org.rmdt.annotation.Rmdt;
import org.rmdt.demo.domain.UserAccount;
import org.rmdt.demo.vo.UserAccountVo;

public interface UserAccountService {


    @Rmdt(destination="member-payment")
    boolean payment(UserAccountVo userAccountVo);

    UserAccount getByUserId(Long userId);
}
