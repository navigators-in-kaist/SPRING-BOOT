package com.navigators.demo.local.save.service;

import java.util.Map;

public interface UserSaveService {

    Map<String, Object> addUserSave(String userId, Map<String, Object> requestBody);
    Map<String, Object> deleteUserSave(String userId, Map<String, Object> requestBody);

}
