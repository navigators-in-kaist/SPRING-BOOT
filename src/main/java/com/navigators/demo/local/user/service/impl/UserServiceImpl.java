package com.navigators.demo.local.user.service.impl;

import com.navigators.demo.adapter.api.keycloak.KeycloakAPI;
import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.codes.UserStatusCode;
import com.navigators.demo.global.dao.user.UserDao;
import com.navigators.demo.global.entity.User;
import com.navigators.demo.local.user.service.UserService;
import com.navigators.demo.util.RandomStringGenerator;
import com.navigators.demo.util.SHA256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private RandomStringGenerator randomStringGenerator = new RandomStringGenerator();
    private final SHA256 _sha256 = new SHA256();

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Map<String, Object> getUserDetail(String userId) {
        Map<String, Object> resultMap = new HashMap<>();

        /* get entity */
        User targetUser = userDao.getUserEntityById(userId).get();

        Map<String, Object> resMap = targetUser.toDto().toMap();

        /* no error */
        resultMap.put("item", resMap);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

    @Override
    public Map<String, Object> editUser(String userId, Map<String, Object> requestBody) {
        Map<String, Object> resultMap = new HashMap<>();

        /* get entity */
        User targetUser = userDao.getUserEntityById(userId).get();

        /* set value */
        targetUser.setUserName((String) requestBody.get("userName"));

        if (requestBody.get("userEmail") != null) {
            /* check is changed */
            if (!targetUser.getUserEmail().equals(requestBody.get("userEmail"))) {
                if (targetUser.isProvenUser()) {
                    /* cannot change! */
                    resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
                    resultMap.put("reason", "The email of the proven users cannot be changed.");
                    resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
                    return resultMap;
                } else {
                    /* can change */
                    targetUser.setUserEmail((String) requestBody.get("userEmail"));
                }
            }
        }

        /* save */
        userDao.saveUser(targetUser.toDto());

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

    @Override
    public Map<String, Object> editPassword(String userId, Map<String, Object> requestBody) {
        Map<String, Object> resultMap = new HashMap<>();

        /** encrypt password */
        String encryptedPassword;
        try {
            encryptedPassword = _sha256.encrypt((String) requestBody.get("newPassword"));
        } catch (Exception e) {
            resultMap.put("errorCode", ErrorCode.PASSWD_ENCRYPT_FAIL);
            resultMap.put("reason", "Failed to encrypt the password.");
            resultMap.put("httpStatusCode", HttpStatus.INTERNAL_SERVER_ERROR);
            return resultMap;
        }

        /** change oauth password */
        /* init keycloak */
        try {
            KeycloakAPI _api = new KeycloakAPI();
            /* init */
            _api.refreshAccessToken();
            /* add user */
            _api.changePassword(_api.getUserUuid(userId), (String) requestBody.get("newPassword"));
        } catch (Exception e) {
            resultMap.put("errorCode", ErrorCode.OAUTH_ADD_USER_FAIL);
            resultMap.put("reason", "Failed to add a new user into auth client.");
            resultMap.put("httpStatusCode", HttpStatus.INTERNAL_SERVER_ERROR);
            return resultMap;
        }

        /* get entity */
        User targetUser = userDao.getUserEntityById(userId).get();

        /* set value */
        targetUser.setUserPassword(encryptedPassword);

        /* save */
        userDao.saveUser(targetUser.toDto());

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }


    @Override
    public Map<String, Object> getUserList() {
        Map<String, Object> resultMap = new HashMap<>();

        List<User> userList = userDao.getUserList();

        List<User> res = userList.stream().filter(user -> user.getUserStatus().equals(UserStatusCode.ACTIVE)).toList();

        Map<String, Object> item = new HashMap<>();
        item.put("userList", res);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

    @Override
    public Map<String, Object> deleteUser(String userId) {
        Map<String, Object> resultMap = new HashMap<>();

        User targetUser = userDao.getUserEntityById(userId).get();

        /* check status */
        if (targetUser.getUserStatus().equals(UserStatusCode.DELETED)) {
            resultMap.put("errorCode", ErrorCode.OAUTH_ADD_USER_FAIL);
            resultMap.put("reason", "Already deleted user!");
            resultMap.put("httpStatusCode", HttpStatus.INTERNAL_SERVER_ERROR);
            return resultMap;
        }

        /* set status */
        String rand = randomStringGenerator.generate(5);
        targetUser.setUserStatus(UserStatusCode.DELETED);
        targetUser.setUserId("deleteduser" + rand);
        targetUser.setUserEmail(null);
        targetUser.setUserPassword("None");
        targetUser.setProvenUser(false);
        targetUser.setUserName("Delete User " + rand);

        userDao.saveUser(targetUser.toDto());

        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }
}
