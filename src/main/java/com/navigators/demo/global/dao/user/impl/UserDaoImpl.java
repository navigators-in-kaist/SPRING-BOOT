package com.navigators.demo.global.dao.user.impl;

import com.navigators.demo.global.dao.user.UserDao;
import com.navigators.demo.global.dto.UserDto;
import com.navigators.demo.global.entity.User;
import com.navigators.demo.global.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDaoImpl implements UserDao {

    private final UserRepository userRepository;

    @Autowired
    public UserDaoImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getUserEntityById(String userId) {
        return userRepository.findByUserId(userId);
    }

    @Override
    public boolean checkDuplication(String field, String payload) {
        if (field.equals("email")) {
            return userRepository.findByUserEmail(payload).isPresent();
        } else { /* id */
            return userRepository.findByUserId(payload).isPresent();
        }
    }

    @Override
    public boolean checkDuplicationExceptSelf(String userId, String field, String payload) {
        if (field.equals("email")) {
            if (userRepository.findByUserEmail(payload).isPresent()) {
                return !userRepository.findByUserEmail(payload).get().getUserId().equals(userId);
            } else {
                return false;
            }
        } else { /* not supported */
            return true;
        }
    }

    @Override
    public void saveUser(UserDto userDto) {
        userRepository.save(userDto.toEntity());
    }
}
