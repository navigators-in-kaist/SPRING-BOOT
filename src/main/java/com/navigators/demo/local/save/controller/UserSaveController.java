package com.navigators.demo.local.save.controller;

import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.local.save.service.UserSaveService;
import com.navigators.demo.util.HeaderParser;
import com.navigators.demo.util.ResponseFormatter;
import com.navigators.demo.validator.DataExistenceValidator;
import com.navigators.demo.validator.JsonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/save")
public class UserSaveController {

    private final HeaderParser parser = new HeaderParser();
    private final JsonValidator validator = new JsonValidator();
    private final ResponseFormatter responseFormatter = new ResponseFormatter();

    private final DataExistenceValidator dataExistenceValidator;
    private final UserSaveService userSaveService;

    @Autowired
    public UserSaveController(DataExistenceValidator dataExistenceValidator,
                              UserSaveService userSaveService) {
        this.dataExistenceValidator = dataExistenceValidator;
        this.userSaveService = userSaveService;
    }

    @PostMapping("")
    public ResponseEntity<Map<String, Object>> addUserSave(@RequestHeader Map<String, String> requestHeader,
                                                           @RequestBody Map<String, Object> requestBody) {

        Map<String, Object> responseBody = new HashMap<>();

        /* request body validation : UserSaveRequestModel.json */
        Map<String, String> validateResultMap = validator.validate("UserSaveRequestModel.json", requestBody);
        if (validateResultMap.get("status").equals("fail") || validateResultMap.get("status").equals("error")) {
            return validator.formatResponseBody(validateResultMap);
        }

        /* check request param */
        /* id */
        if (requestBody.get("kind").equals("building")) {
            if (!dataExistenceValidator.isBuildingExist((String) requestBody.get("id"))) {
                responseBody.put("error_code", ErrorCode.POST_INVALID_PARAM);
                responseBody.put("reason", "The building having the given \"id\" does not exists.");
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }
        } else { /* location */
            if (!dataExistenceValidator.isLocationExist((String) requestBody.get("id"))) {
                responseBody.put("error_code", ErrorCode.POST_INVALID_PARAM);
                responseBody.put("reason", "The location having the given \"id\" does not exists.");
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }
        }

        /* get userId */
        String userId = "";
        userId = parser.getUserIdByAccessToken(requestHeader.get("authorization"));
        if (!dataExistenceValidator.isUserExist(userId)) {
            responseBody.put("error_code", ErrorCode.GET_INVALID_PARAM);
            responseBody.put("reason", "The given user does not exists.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        /* service call */
        Map<String, Object> resultMap = userSaveService.addUserSave(userId, requestBody);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @PostMapping("/undo")
    public ResponseEntity<Map<String, Object>> deleteUserSave(@RequestHeader Map<String, String> requestHeader,
                                                           @RequestBody Map<String, Object> requestBody) {

        Map<String, Object> responseBody = new HashMap<>();

        /* request body validation : UserSaveRequestModel.json */
        Map<String, String> validateResultMap = validator.validate("UserSaveRequestModel.json", requestBody);
        if (validateResultMap.get("status").equals("fail") || validateResultMap.get("status").equals("error")) {
            return validator.formatResponseBody(validateResultMap);
        }

        /* check request param */
        /* id */
        if (requestBody.get("kind").equals("building")) {
            if (!dataExistenceValidator.isBuildingExist((String) requestBody.get("id"))) {
                responseBody.put("error_code", ErrorCode.POST_INVALID_PARAM);
                responseBody.put("reason", "The building having the given \"id\" does not exists.");
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }
        } else { /* location */
            if (!dataExistenceValidator.isLocationExist((String) requestBody.get("id"))) {
                responseBody.put("error_code", ErrorCode.POST_INVALID_PARAM);
                responseBody.put("reason", "The location having the given \"id\" does not exists.");
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }
        }

        /* get userId */
        String userId = "";
        userId = parser.getUserIdByAccessToken(requestHeader.get("authorization"));
        if (!dataExistenceValidator.isUserExist(userId)) {
            responseBody.put("error_code", ErrorCode.GET_INVALID_PARAM);
            responseBody.put("reason", "The given user does not exists.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        /* service call */
        Map<String, Object> resultMap = userSaveService.deleteUserSave(userId, requestBody);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }

}
