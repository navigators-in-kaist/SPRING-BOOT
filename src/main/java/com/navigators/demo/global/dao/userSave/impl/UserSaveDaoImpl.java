package com.navigators.demo.global.dao.userSave.impl;

import com.navigators.demo.global.dao.userSave.UserSaveDao;
import com.navigators.demo.global.dto.UserSaveDto;
import com.navigators.demo.global.entity.UserSave;
import com.navigators.demo.global.repository.UserSaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserSaveDaoImpl implements UserSaveDao {

    private final UserSaveRepository userSaveRepository;

    @Autowired
    public UserSaveDaoImpl(UserSaveRepository userSaveRepository) {
        this.userSaveRepository = userSaveRepository;
    }

    @Override
    public Optional<UserSave> getEntityById(String userSaveId) {
        return userSaveRepository.findById(userSaveId);
    }

    @Override
    public void saveDto(UserSaveDto userSaveDto) {
        userSaveRepository.save(userSaveDto.toEntity());
    }

    @Override
    public void deleteDto(UserSaveDto userSaveDto) {
        userSaveRepository.delete(userSaveDto.toEntity());
    }

    @Override
    public List<UserSave> getSaveListByBuildingId(String buildingId) {
        return userSaveRepository.findByUserSaveBuildingId(buildingId);
    }

    @Override
    public List<UserSave> getSaveListByLocationId(String locationId) {
        return userSaveRepository.findByUserSaveLocationId(locationId);
    }

}
