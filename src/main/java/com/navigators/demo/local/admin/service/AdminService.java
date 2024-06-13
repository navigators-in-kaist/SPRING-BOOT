package com.navigators.demo.local.admin.service;

import java.util.Map;

public interface AdminService {

    Map<String, Object> getAdminList();
    Map<String, Object> addAdmin(Map<String, Object> requestBody);
    Map<String, Object> editAdmin(String adminId, Map<String, Object> requestBody);
    Map<String, Object> inactivateAdmin(String adminId);

}
