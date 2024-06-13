package com.navigators.demo.global.dao.admin;

import com.navigators.demo.global.dto.AdminDto;
import com.navigators.demo.global.entity.Admin;

import java.util.List;
import java.util.Optional;

public interface AdminDao {

    Optional<Admin> getAdminEntityById(String adminId);

    List<Admin> getAdminList();

    void saveDto(AdminDto adminDto);

}
