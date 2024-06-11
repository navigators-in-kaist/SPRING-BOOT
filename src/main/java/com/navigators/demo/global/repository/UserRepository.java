package com.navigators.demo.global.repository;

import com.navigators.demo.global.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUserId(String userId);
    Optional<User> findByUserEmail(String email);

}
