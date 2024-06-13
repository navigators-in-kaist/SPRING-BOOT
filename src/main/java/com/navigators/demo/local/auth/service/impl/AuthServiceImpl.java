package com.navigators.demo.local.auth.service.impl;

import com.navigators.demo.adapter.api.keycloak.KeycloakAPI;
import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.codes.UserStatusCode;
import com.navigators.demo.global.dao.admin.AdminDao;
import com.navigators.demo.global.dao.emailSchedule.EmailScheduleDao;
import com.navigators.demo.global.dao.proveTrial.ProveTrialDao;
import com.navigators.demo.global.dao.user.UserDao;
import com.navigators.demo.global.dto.EmailScheduleDto;
import com.navigators.demo.global.dto.ProveTrialDto;
import com.navigators.demo.global.dto.UserDto;
import com.navigators.demo.global.entity.Admin;
import com.navigators.demo.global.entity.EmailSchedule;
import com.navigators.demo.global.entity.ProveTrial;
import com.navigators.demo.global.entity.User;
import com.navigators.demo.local.auth.service.AuthService;
import com.navigators.demo.util.EmailContentGenerator;
import com.navigators.demo.util.SHA256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final SHA256 _sha256 = new SHA256();
    private final EmailContentGenerator emailContentGenerator = new EmailContentGenerator();
    private final UserDao userDao;
    private final AdminDao adminDao;
    private final ProveTrialDao proveTrialDao;
    private final EmailScheduleDao emailScheduleDao;

    @Autowired
    public AuthServiceImpl(UserDao userDao,
                           AdminDao adminDao,
                           ProveTrialDao proveTrialDao,
                           EmailScheduleDao emailScheduleDao) {
        this.userDao = userDao;
        this.adminDao = adminDao;
        this.proveTrialDao = proveTrialDao;
        this.emailScheduleDao = emailScheduleDao;
    }


    private static boolean isSpecificDomain(String email, String domain) {
        if (email == null || domain == null) {
            return false;
        }

        // Ensure the domain starts with '@'
        if (!domain.startsWith("@")) {
            domain = "@" + domain;
        }

        return email.endsWith(domain);
    }

    @Override
    public boolean checkIdAndPassword(String id, String password) {
        User targetUser = userDao.getUserEntityById(id).get();
        String encryptedPassword;
        try {
            encryptedPassword =  _sha256.encrypt(password);
        } catch (Exception e) {
            encryptedPassword = "FAILED";
        }

        return targetUser.getUserPassword().equals(encryptedPassword);
    }

    @Override
    public boolean checkIdAndPasswordForAdmin(String id, String password) {
        Admin targetAdmin = adminDao.getAdminEntityById(id).get();
        String encryptedPassword;
        try {
            encryptedPassword =  _sha256.encrypt(password);
        } catch (Exception e) {
            encryptedPassword = "FAILED";
        }

        return targetAdmin.getAdminPassword().equals(encryptedPassword);
    }

    @Override
    public Map<String, Object> addUser(Map<String, Object> requestBody) {
        Map<String, Object> resultMap = new HashMap<>();

        /* check id duplication */
        if (userDao.checkDuplication("id", (String) requestBody.get("userId"))) {
            resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
            resultMap.put("reason", "Already existing id.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /* check email duplication */
        if ((requestBody.get("userEmail") != null)
                && (userDao.checkDuplication("email", (String) requestBody.get("userEmail")))) {
            resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
            resultMap.put("reason", "Already existing email.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /** encrypt password */
        String encryptedPassword;
        try {
            encryptedPassword = _sha256.encrypt((String) requestBody.get("password"));
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
            /* add user */
            _api.addUser((String) requestBody.get("userId"), (String) requestBody.get("password"));
            /* role mapping */
            _api.addAppClientRoleMappingForUnProven(_api.getUserUuid((String) requestBody.get("userId")));
        } catch (Exception e) {
            resultMap.put("errorCode", ErrorCode.OAUTH_ADD_USER_FAIL);
            resultMap.put("reason", "Failed to add a new user into auth client.");
            resultMap.put("httpStatusCode", HttpStatus.INTERNAL_SERVER_ERROR);
            return resultMap;
        }

        /** add to the database */
        /* craft a dto */
        UserDto targetDto = UserDto.builder()
                .userUuid(UUID.randomUUID().toString())
                .userId((String) requestBody.get("userId"))
                .userName((String) requestBody.get("userName"))
                .userPassword(encryptedPassword)
                .userStatus(UserStatusCode.ACTIVE)
                .isProvenUser(false)
                .build();

        if (requestBody.get("userEmail") != null) {
            targetDto.setUserEmail((String) requestBody.get("userEmail"));
        }

        /* save */
        userDao.saveUser(targetDto);

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

    @Override
    public Map<String, Object> tryProve(String userId) {
        Map<String, Object> resultMap = new HashMap<>();

        /** check user */
        /* get target user */
        User targetUser = userDao.getUserEntityById(userId).get();

        /* user status check */
        if (!targetUser.getUserStatus().equals(UserStatusCode.ACTIVE)) {
            resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
            resultMap.put("reason", "The given user is not in active status.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /* user email existence */
        if (targetUser.getUserEmail() == null) {
            resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
            resultMap.put("reason", "The given user has no email address.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /* user isProven check */
        if (targetUser.isProvenUser()) {
            resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
            resultMap.put("reason", "The given user is already proved.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /* user already having trial check */
        List<ProveTrial> proveTrialList = proveTrialDao.getProveTrialLisOfUser(userId);
        if (!proveTrialList.isEmpty()) {
            resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
            resultMap.put("reason", "The given user is already in proving process.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /* user email domain check */
        if (!this.isSpecificDomain(targetUser.getUserEmail(), "@kaist.ac.kr")) {
            resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
            resultMap.put("reason", "The email address of given user is NOT KAIST email.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /** register trial */
        Map<String, String> dataMap = emailContentGenerator.makeVerifyCodeAndEmailContent();
        ProveTrialDto newProveTrialDto = ProveTrialDto.builder()
                .trialId(targetUser.getUserUuid() + dataMap.get("code"))
                .createdAt(LocalDateTime.now())
                .verifyCode(dataMap.get("code"))
                .proveTrialUserId(targetUser.getUserUuid())
                .build();
        proveTrialDao.saveDto(newProveTrialDto);

        /** register email schedule */
        EmailScheduleDto newEmailScheduleDto = EmailScheduleDto.builder()
                .scheduleId(UUID.randomUUID().toString())
                .content(dataMap.get("content"))
                .sendTrialCount(0)
                .emailAddr(targetUser.getUserEmail())
                .isSuccess(false)
                .emailScheduleProveTrialId(newProveTrialDto.getTrialId())
                .build();
        emailScheduleDao.saveDto(newEmailScheduleDto);

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("generatedProveTrialId", newProveTrialDto.getTrialId());
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

    @Override
    public Map<String, Object> submitProve(String userId, String trialId, String verifyCode) {
        Map<String, Object> resultMap = new HashMap<>();

        /** check */
        /* get target user */
        User targetUser = userDao.getUserEntityById(userId).get();

        /* user and trial combination check */
        ProveTrialDto proveTrialDto = proveTrialDao.getEntityById(trialId).get().toDto();
        if (!proveTrialDto.getProveTrialUserId().equals(targetUser.getUserUuid())) {
            resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
            resultMap.put("reason", "The given trial is not of the given user.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /* trial verify code check */
        if (!proveTrialDto.getVerifyCode().equals(verifyCode)) {
            resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
            resultMap.put("reason", "The given verify code is incorrect.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /** delete trial */
        /* get email schedules */
        List<EmailSchedule> emailScheduleList = emailScheduleDao.getEmailScheduleListByTrialId(trialId);
        if (!emailScheduleList.isEmpty()) {
            /* delete schedule */
            for (EmailSchedule emailSchedule : emailScheduleList) {
                emailScheduleDao.deleteDto(emailSchedule.toDto());
            }
        }
        proveTrialDao.deleteDto(proveTrialDto);

        /** update user information */
        /* assign keycloak role */
        try {
            KeycloakAPI _api = new KeycloakAPI();
            /* init */
            _api.refreshAccessToken();
            /* get user keycloak uuid */
            String kUuid = _api.getUserUuid(userId);
            /* delete prev role */
            _api.deleteClientRoleMapping(kUuid);
            /* assign new role */
            _api.addAppClientRoleMappingForProven(kUuid);
        } catch (Exception e) {
            resultMap.put("errorCode", ErrorCode.OAUTH_EDIT_USER_FAIL);
            resultMap.put("reason", "Failed to set oauth info. " + e.getMessage());
            resultMap.put("httpStatusCode", HttpStatus.INTERNAL_SERVER_ERROR);
            return resultMap;
        }

        /* edit user info */
        /* update user isProven */
        targetUser.setProvenUser(true);
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
    public Map<String, Object> checkDuplication(String field, String payload) {
        Map<String, Object> resultMap = new HashMap<>();
        boolean result = userDao.checkDuplication(field, payload);

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("isDuplicated", result);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }
}
