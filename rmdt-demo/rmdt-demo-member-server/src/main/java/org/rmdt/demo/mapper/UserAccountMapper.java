package org.rmdt.demo.mapper;

import org.rmdt.demo.domain.UserAccount;
import java.util.List;

public interface UserAccountMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserAccount record);

    UserAccount selectByPrimaryKey(Long id);

    List<UserAccount> selectAll();

    int updateByPrimaryKey(UserAccount record);

    UserAccount getByUserId(Long userId);

    void updateByUserId(UserAccount userAccount);
}