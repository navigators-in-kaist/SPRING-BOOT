package com.navigators.demo.global.dao.userSave;

import com.navigators.demo.global.dto.UserSaveDto;
import com.navigators.demo.global.entity.UserSave;

import java.util.List;
import java.util.Optional;

public interface UserSaveDao {

    Optional<UserSave> getEntityById(String userSaveId);

    void saveDto(UserSaveDto userSaveDto);
    void deleteDto(UserSaveDto userSaveDto);

    List<UserSave> getSaveListByBuildingId(String buildingId);
    List<UserSave> getSaveListByLocationId(String locationId);

}
