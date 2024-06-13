package com.navigators.demo.global.dao.user;

import com.navigators.demo.global.dto.UserDto;
import com.navigators.demo.global.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> getUserEntityById(String userId);

    boolean checkDuplication(String field, String payload);
    boolean checkDuplicationExceptSelf(String userId, String field, String payload);

    void saveUser(UserDto userDto);

    List<User> getUserList();
}
