package com.navigators.demo.local.save.service.impl;

import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.global.dao.user.UserDao;
import com.navigators.demo.global.dao.userSave.UserSaveDao;
import com.navigators.demo.global.dto.UserSaveDto;
import com.navigators.demo.global.entity.User;
import com.navigators.demo.local.save.service.UserSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserSaveServiceImpl implements UserSaveService {

    private final UserDao userDao;
    private final UserSaveDao userSaveDao;

    @Autowired
    public UserSaveServiceImpl(UserDao userDao,
                               UserSaveDao userSaveDao) {
        this.userDao = userDao;
        this.userSaveDao = userSaveDao;
    }

    @Override
    public Map<String, Object> addUserSave(String userId, Map<String, Object> requestBody) {
        Map<String, Object> resultMap = new HashMap<>();

        /* target user */
        User targetUser = userDao.getUserEntityById(userId).get();

        /* check already existing */
        String pseudoId = targetUser.getUserUuid() + requestBody.get("id");
        if (userSaveDao.getEntityById(pseudoId).isPresent()) {
            resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
            resultMap.put("reason", "The given data is already saved by the user.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /* craft a new dto */
        UserSaveDto userSaveDto = UserSaveDto.builder()
                .mappingId(pseudoId)
                .userSaveUserId(targetUser.getUserUuid())
                .build();

        if (requestBody.get("kind").equals("building")) {
            userSaveDto.setUserSaveBuildingId((String) requestBody.get("id"));
        } else {
            userSaveDto.setUserSaveLocationId((String) requestBody.get("id"));
        }

        userSaveDao.saveDto(userSaveDto);

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

    @Override
    public Map<String, Object> deleteUserSave(String userId, Map<String, Object> requestBody) {
        Map<String, Object> resultMap = new HashMap<>();

        /* target user */
        User targetUser = userDao.getUserEntityById(userId).get();

        /* check existing */
        String pseudoId = targetUser.getUserUuid() + requestBody.get("id");
        if (!userSaveDao.getEntityById(pseudoId).isPresent()) {
            resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
            resultMap.put("reason", "The user have no save for the given \"id\".");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /* delete */
        userSaveDao.deleteDto(userSaveDao.getEntityById(pseudoId).get().toDto());

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

}
