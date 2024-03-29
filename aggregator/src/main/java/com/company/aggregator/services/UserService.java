package com.company.aggregator.services;

import com.company.aggregator.dtos.ChangePasswordDto;
import com.company.aggregator.dtos.UserLockStatusDto;
import com.company.aggregator.models.Image;
import com.company.aggregator.models.User;
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
