package com.company.aggregator.service;

import com.company.aggregator.dto.ChangePasswordDto;
import com.company.aggregator.dto.UserLockStatusDto;
import com.company.aggregator.entity.Image;
import com.company.aggregator.entity.User;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface UserService {
    List<User> findUsers(PageRequest pageRequest);

    User findUserByUsername(String username);

    void saveUser(User user);

    void changePassword(User user, ChangePasswordDto changePasswordDto);

    void uploadAvatar(User user, Image avatar);

    void block(UserLockStatusDto userLockStatusDto);

    void unblock(UserLockStatusDto userLockStatusDto);
}
