package com.navigators.demo.local.user.service;

import java.util.Map;

public interface UserService {

    Map<String, Object> getUserDetail(String userId);
    Map<String, Object> editUser(String userId, Map<String, Object> requestBody);
    Map<String, Object> editPassword(String userId, Map<String, Object> requestBody);

}
