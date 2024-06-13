package com.navigators.demo.global.dao.admin.impl;

import com.navigators.demo.global.dao.admin.AdminDao;
import com.navigators.demo.global.dto.AdminDto;
import com.navigators.demo.global.entity.Admin;
import com.navigators.demo.global.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AdminDaoImpl implements AdminDao {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminDaoImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public Optional<Admin> getAdminEntityById(String adminId) {
        return adminRepository.findByAdminId(adminId);
    }

    @Override
    public List<Admin> getAdminList() {
        return adminRepository.findAll();
    }

    @Override
    public void saveDto(AdminDto adminDto) {
        adminRepository.save(adminDto.toEntity());
    }
}
