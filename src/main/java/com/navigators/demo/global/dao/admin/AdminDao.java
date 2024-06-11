package com.navigators.demo.global.dao.admin;

import com.navigators.demo.global.entity.Admin;

import java.util.Optional;

public interface AdminDao {

    Optional<Admin> getAdminEntityById(String adminId);

}
