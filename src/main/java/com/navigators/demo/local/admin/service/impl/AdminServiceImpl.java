package com.navigators.demo.local.admin.service.impl;

import com.navigators.demo.adapter.api.keycloak.KeycloakAPI;
import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.global.dao.admin.AdminDao;
import com.navigators.demo.global.dto.AdminDto;
import com.navigators.demo.global.entity.Admin;
import com.navigators.demo.local.admin.service.AdminService;
import com.navigators.demo.util.RandomStringGenerator;
import com.navigators.demo.util.SHA256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminServiceImpl implements AdminService {

    private final SHA256 _sha256 = new SHA256();
    private final RandomStringGenerator randomStringGenerator = new RandomStringGenerator();
    private final AdminDao adminDao;

    @Autowired
    public AdminServiceImpl(AdminDao adminDao) {
        this.adminDao = adminDao;
    }

    @Override
    public Map<String, Object> getAdminList() {
        Map<String, Object> resultMap = new HashMap<>();

        /* get total list */
        List<Admin> adminList = adminDao.getAdminList();

        /* filter out disabled */
        List<Admin> resultList = adminList.stream().filter(admin -> !admin.getAdminName().startsWith("_Deleted")).toList();

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("adminList", resultList);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }


    @Override
    public Map<String, Object> addAdmin(Map<String, Object> requestBody) {
        Map<String, Object> resultMap = new HashMap<>();

        /** encrypt password */
        String encryptedPassword;
        try {
            encryptedPassword = _sha256.encrypt((String) requestBody.get("adminPassword"));
        } catch (Exception e) {
            resultMap.put("errorCode", ErrorCode.PASSWD_ENCRYPT_FAIL);
            resultMap.put("reason", "Failed to encrypt the password.");
            resultMap.put("httpStatusCode", HttpStatus.INTERNAL_SERVER_ERROR);
            return resultMap;
        }

        /** add a new oauth user */
        /* init keycloak */
        try {
            KeycloakAPI _api = new KeycloakAPI();
            /* init */
            _api.refreshAccessToken();
            /* add admin */
            _api.addUser((String) requestBody.get("adminId"), (String) requestBody.get("adminPassword"));
            /* role mapping */
            _api.addAppClientRoleMappingForAdmin(_api.getUserUuid((String) requestBody.get("adminId")));
        } catch (Exception e) {
            resultMap.put("errorCode", ErrorCode.OAUTH_ADD_USER_FAIL);
            resultMap.put("reason", "Failed to add a new admin into auth client.");
            resultMap.put("httpStatusCode", HttpStatus.INTERNAL_SERVER_ERROR);
            return resultMap;
        }

        /** add to the DB */
        AdminDto adminDto = AdminDto.builder()
                .adminUuid(UUID.randomUUID().toString())
                .adminId((String) requestBody.get("adminId"))
                .adminName((String) requestBody.get("adminName"))
                .adminPassword(encryptedPassword)
                .build();

        adminDao.saveDto(adminDto);

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }


    @Override
    public Map<String, Object> editAdmin(String adminId, Map<String, Object> requestBody) {
        Map<String, Object> resultMap = new HashMap<>();

        /* get target entity */
        Admin targetAdmin = adminDao.getAdminEntityById(adminId).get();

        /* set value */
        targetAdmin.setAdminName((String) requestBody.get("adminName"));

        /* save */
        adminDao.saveDto(targetAdmin.toDto());

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }


    @Override
    public Map<String, Object> inactivateAdmin(String adminId) {
        Map<String, Object> resultMap = new HashMap<>();

        /* get target entity */
        Admin targetAdmin = adminDao.getAdminEntityById(adminId).get();

        /* check status */
        if (targetAdmin.getAdminName().startsWith("_Deleted")) {
            resultMap.put("errorCode", ErrorCode.DELETE_INVALID_PARAM);
            resultMap.put("reason", "The given admin is already inactivated.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /* set values */
        targetAdmin.setAdminName("_Deleted Admin " + randomStringGenerator.generate(5));

        /* save */
        adminDao.saveDto(targetAdmin.toDto());

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

}
