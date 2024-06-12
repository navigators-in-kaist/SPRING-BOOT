package com.navigators.demo.global.dao.userSave;

import com.navigators.demo.global.dto.UserSaveDto;
import com.navigators.demo.global.entity.UserSave;

import java.util.Optional;

public interface UserSaveDao {

    Optional<UserSave> getEntityById(String userSaveId);

    void saveDto(UserSaveDto userSaveDto);
    void deleteDto(UserSaveDto userSaveDto);

}
