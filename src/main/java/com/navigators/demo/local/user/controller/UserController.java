package com.navigators.demo.local.user.controller;

import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.local.user.service.UserService;
import com.navigators.demo.util.HeaderParser;
import com.navigators.demo.util.ResponseFormatter;
import com.navigators.demo.validator.DataExistenceValidator;
import com.navigators.demo.validator.JsonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/** Note. adding user functionality is in /auth endpoint */
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final HeaderParser parser = new HeaderParser();
    private final JsonValidator validator = new JsonValidator();
    private final ResponseFormatter responseFormatter = new ResponseFormatter();

    private final DataExistenceValidator dataExistenceValidator;
    private final UserService userService;

    @Autowired
    public UserController(DataExistenceValidator dataExistenceValidator,
                          UserService userService) {
        this.dataExistenceValidator = dataExistenceValidator;
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserInfo(@PathVariable String userId) {
        Map<String, Object> responseBody = new HashMap<>();

        /* check path variable */
        if (!dataExistenceValidator.isUserExist(userId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

        /* service call */
        Map<String, Object> resultMap = userService.getUserDetail(userId);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> editUserInfo(@RequestHeader Map<String, String> requestHeader,
                                                            @PathVariable String userId,
                                                            @RequestBody Map<String, Object> requestBody) {
        Map<String, Object> responseBody = new HashMap<>();

        /* check path variable */
        if (!dataExistenceValidator.isUserExist(userId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

        /* request body validation : UserModel.json */
        Map<String, String> validateResultMap = validator.validate("UserModel.json", requestBody);
        if (validateResultMap.get("status").equals("fail") || validateResultMap.get("status").equals("error")) {
            return validator.formatResponseBody(validateResultMap);
        }

        String coverage = "";
        /* coverage */
        String roleName = parser.getRoleNameByAccessToken(requestHeader.get("kauthorization"));
        if (roleName.equals("None")) {
            responseBody.put("error_code", ErrorCode.ROLE_UNAUTHORIZED);
            responseBody.put("reason", "The given request header does not have role information.");
            return new ResponseEntity<>(responseBody, HttpStatus.UNAUTHORIZED);
        }

        if (roleName.equals("admin")) {
            coverage = "all";
        } else {
            coverage = "user";
        }

        if (coverage.equals("user")) {
            /* get userId */
            String requestUserId = parser.getUserIdByAccessToken(requestHeader.get("kauthorization"));
            if (!dataExistenceValidator.isUserExist(userId)) {
                responseBody.put("error_code", ErrorCode.PUT_INVALID_PARAM);
                responseBody.put("reason", "The given user (in token) does not exists.");
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }

            /* compare */
            if (!requestUserId.equals(userId)) {
                responseBody.put("error_code", ErrorCode.PUT_INVALID_PARAM);
                responseBody.put("reason", "The user can edit his/her own information.");
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }
        }

        /* service call */
        Map<String, Object> resultMap = userService.editUser(userId, requestBody);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @PutMapping("/{userId}/passwd")
    public ResponseEntity<Map<String, Object>> editUserPassword(@RequestHeader Map<String, String> requestHeader,
                                                                @PathVariable String userId,
                                                                @RequestBody Map<String, Object> requestBody) {
        Map<String, Object> responseBody = new HashMap<>();

        /* check path variable */
        if (!dataExistenceValidator.isUserExist(userId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

        /* request body validation : UserPasswordModel.json */
        Map<String, String> validateResultMap = validator.validate("UserPasswordModel.json", requestBody);
        if (validateResultMap.get("status").equals("fail") || validateResultMap.get("status").equals("error")) {
            return validator.formatResponseBody(validateResultMap);
        }

        String coverage = "";
        /* coverage */
        String roleName = parser.getRoleNameByAccessToken(requestHeader.get("kauthorization"));
        if (roleName.equals("None")) {
            responseBody.put("error_code", ErrorCode.ROLE_UNAUTHORIZED);
            responseBody.put("reason", "The given request header does not have role information.");
            return new ResponseEntity<>(responseBody, HttpStatus.UNAUTHORIZED);
        }

        if (roleName.equals("admin")) {
            coverage = "all";
        } else {
            coverage = "user";
        }

        if (coverage.equals("user")) {
            /* get userId */
            String requestUserId = parser.getUserIdByAccessToken(requestHeader.get("kauthorization"));
            if (!dataExistenceValidator.isUserExist(userId)) {
                responseBody.put("error_code", ErrorCode.PUT_INVALID_PARAM);
                responseBody.put("reason", "The given user (in token) does not exists.");
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }

            /* compare */
            if (!requestUserId.equals(userId)) {
                responseBody.put("error_code", ErrorCode.PUT_INVALID_PARAM);
                responseBody.put("reason", "The user can edit his/her own information.");
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }
        }

        /* service call */
        Map<String, Object> resultMap = userService.editPassword(userId, requestBody);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getUserList() {
        Map<String, Object> responseBody = new HashMap<>();

        /* service call */
        Map<String, Object> resultMap = userService.getUserList();
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String userId) {
        Map<String, Object> responseBody = new HashMap<>();

        /* check path variable */
        if (!dataExistenceValidator.isUserExist(userId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

        /* service call */
        Map<String, Object> resultMap = userService.deleteUser(userId);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }
}
