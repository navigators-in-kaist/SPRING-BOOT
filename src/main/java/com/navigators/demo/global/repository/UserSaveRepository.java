package com.navigators.demo.global.repository;

import com.navigators.demo.global.entity.UserSave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSaveRepository extends JpaRepository<UserSave, String> {

    Optional<UserSave> findById(String saveId);

    List<UserSave> findByUserSaveBuildingId(String buildingId);
    List<UserSave> findByUserSaveLocationId(String locationId);
}
