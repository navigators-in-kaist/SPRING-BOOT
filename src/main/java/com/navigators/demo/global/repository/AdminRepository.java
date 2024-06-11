package com.navigators.demo.global.repository;

import com.navigators.demo.global.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, String> {

    Optional<Admin> findByAdminId(String adminId);

}
