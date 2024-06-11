package com.navigators.demo.local.auth.service;

import java.util.Map;

public interface AuthService {

    boolean checkIdAndPassword(String id, String password);
    boolean checkIdAndPasswordForAdmin(String id, String password);

    Map<String, Object> addUser(Map<String, Object> requestBody);
    Map<String, Object> tryProve(String userId);
    Map<String, Object> submitProve(String userId, String trialId, String verifyCode);
    Map<String, Object> checkDuplication(String field, String payload);
}
