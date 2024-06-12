package com.navigators.demo.local.auth.controller;

import com.navigators.demo.adapter.api.keycloak.KeycloakAPI;
import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.local.auth.service.AuthService;
import com.navigators.demo.util.HeaderParser;
import com.navigators.demo.util.ResponseFormatter;
import com.navigators.demo.validator.DataExistenceValidator;
import com.navigators.demo.validator.JsonValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final HeaderParser parser = new HeaderParser();
    private final JsonValidator validator = new JsonValidator();
    private final ResponseFormatter responseFormatter = new ResponseFormatter();

    private final DataExistenceValidator dataExistenceValidator;
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService,
                          DataExistenceValidator dataExistenceValidator) {
        this.authService = authService;
        this.dataExistenceValidator = dataExistenceValidator;
    }

    @PostMapping("/login/admin")
    public ResponseEntity<Map<String, Object>> loginFromAdmin(@RequestBody Map<String, Object> requestBody) {
        Map<String, Object> responseBody = new HashMap<>();

        String adminId = (String) requestBody.get("adminId");
        String password = (String) requestBody.get("password");

        if (adminId == null) {
            responseBody.put("error_code", ErrorCode.POST_INVALID_PARAM);
            responseBody.put("reason", "The adminId is a mandatory field.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        if (password == null) {
            responseBody.put("error_code", ErrorCode.POST_INVALID_PARAM);
            responseBody.put("reason", "The admin password is a mandatory field.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        if (!dataExistenceValidator.isAdminExist(adminId)) {
            responseBody.put("error_code", ErrorCode.POST_INVALID_PARAM);
            responseBody.put("reason", "The given admin does not exists.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        if (!authService.checkIdAndPasswordForAdmin(adminId, password)) {
            responseBody.put("error_code", ErrorCode.ROLE_UNAUTHORIZED);
            responseBody.put("reason", "The password is incorrect.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        /* call keycloak */
        Map<String, String> resultMap;
        try {
            KeycloakAPI _api = new KeycloakAPI();
            /* init */
            _api.refreshAccessToken();
            resultMap = _api.loginUser(adminId, password);
        } catch (Exception e) {
            responseBody.put("error_code", ErrorCode.OAUTH_GET_TOKEN_FAIL);
            responseBody.put("reason", "The error occur while connecting with auth client. : " + e.getMessage());
            return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        responseBody.put("error_code", ErrorCode.NO_ERROR);
        responseBody.put("item", resultMap);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping("/login/user")
    public ResponseEntity<Map<String, Object>> loginFromUser(@RequestBody Map<String, Object> requestBody) {
        Map<String, Object> responseBody = new HashMap<>();

        String userId = (String) requestBody.get("userId");
        String password = (String) requestBody.get("password");

        if (userId == null) {
            responseBody.put("error_code", ErrorCode.POST_INVALID_PARAM);
            responseBody.put("reason", "The userId is a mandatory field.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        if (password == null) {
            responseBody.put("error_code", ErrorCode.POST_INVALID_PARAM);
            responseBody.put("reason", "The user password is a mandatory field.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        if (!dataExistenceValidator.isUserExist(userId)) {
            responseBody.put("error_code", ErrorCode.POST_INVALID_PARAM);
            responseBody.put("reason", "The given user does not exists.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        if (!authService.checkIdAndPassword(userId, password)) {
            responseBody.put("error_code", ErrorCode.ROLE_UNAUTHORIZED);
            responseBody.put("reason", "The password is incorrect.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        /* call keycloak */
        Map<String, String> resultMap;
        try {
            KeycloakAPI _api = new KeycloakAPI();
            /* init */
            _api.refreshAccessToken();
            resultMap = _api.loginUser(userId, password);
        } catch (Exception e) {
            responseBody.put("error_code", ErrorCode.OAUTH_GET_TOKEN_FAIL);
            responseBody.put("reason", "The error occur while connecting with auth client. : " + e.getMessage());
            return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        responseBody.put("error_code", ErrorCode.NO_ERROR);
        responseBody.put("item", resultMap);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }


    @PostMapping("/sign-up/user")
    public ResponseEntity<Map<String, Object>> signUpUser(@RequestBody Map<String, Object> requestBody) {
        Map<String, Object> responseBody = new HashMap<>();

        /* request body validation : UserModel.json */
        Map<String, String> validateResultMap = validator.validate("UserModel.json", requestBody);
        if (validateResultMap.get("status").equals("fail") || validateResultMap.get("status").equals("error")) {
            return validator.formatResponseBody(validateResultMap);
        }

        /* check additional field */
        if (requestBody.get("password") == null) {
            responseBody.put("error_code", ErrorCode.POST_INVALID_PARAM);
            responseBody.put("reason", "The user password is a mandatory field.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        /* service call */
        Map<String, Object> resultMap = authService.addUser(requestBody);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @PostMapping("/prove")
    public ResponseEntity<Map<String, Object>> tryProve(@RequestHeader Map<String, String> requestHeader) {
        Map<String, Object> responseBody = new HashMap<>();

        /* check user existence */
        String userId = parser.getUserIdByAccessToken(requestHeader.get("kauthorization"));
        if (!dataExistenceValidator.isUserExist(userId)) {
            responseBody.put("error_code", ErrorCode.POST_INVALID_PARAM);
            responseBody.put("reason", "The given user does not exists.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        /* service call */
        Map<String, Object> resultMap = authService.tryProve(userId);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }

    @PostMapping("/prove/{trialId}")
    public ResponseEntity<Map<String, Object>> submitProve(@RequestHeader Map<String, String> requestHeader,
                                                           @RequestBody Map<String, String> requestBody,
                                                           @PathVariable String trialId) {
        Map<String, Object> responseBody = new HashMap<>();

        /* check path variable */
        if (!dataExistenceValidator.isProveTrialExist(trialId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

        /* check user existence */
        String userId = parser.getUserIdByAccessToken(requestHeader.get("kauthorization"));
        if (!dataExistenceValidator.isUserExist(userId)) {
            responseBody.put("error_code", ErrorCode.POST_INVALID_PARAM);
            responseBody.put("reason", "The given user does not exists.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        /* check request body param */
        if (requestBody.get("verifyCode") == null) {
            responseBody.put("error_code", ErrorCode.POST_INVALID_PARAM);
            responseBody.put("reason", "The verify code is a mandatory field.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        /* service call */
        Map<String, Object> resultMap = authService.submitProve(userId, trialId, requestBody.get("verifyCode"));
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }

    @GetMapping("/sign-up/user/duplication/email")
    public ResponseEntity<Map<String, Object>> checkEmailDup(@RequestParam String payload) {
        Map<String, Object> responseBody = new HashMap<>();

        /* service call */
        Map<String, Object> resultMap = authService.checkDuplication("email", payload);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }

    @GetMapping("/sign-up/user/duplication/id")
    public ResponseEntity<Map<String, Object>> checkIdDup(@RequestParam String payload) {
        Map<String, Object> responseBody = new HashMap<>();

        /* service call */
        Map<String, Object> resultMap = authService.checkDuplication("id", payload);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }
}
