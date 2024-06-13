package com.navigators.demo.local.admin.controller;

import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.global.entity.Admin;
import com.navigators.demo.local.admin.service.AdminService;
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

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final HeaderParser parser = new HeaderParser();
    private final JsonValidator validator = new JsonValidator();
    private final ResponseFormatter responseFormatter = new ResponseFormatter();

    private final DataExistenceValidator dataExistenceValidator;
    private AdminService adminService;

    @Autowired
    public AdminController(DataExistenceValidator dataExistenceValidator,
                           AdminService adminService) {
        this.dataExistenceValidator = dataExistenceValidator;
        this.adminService = adminService;
    }


    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAdminList() {
        Map<String, Object> responseBody = new HashMap<>();

        /* service call */
        Map<String, Object> resultMap = adminService.getAdminList();
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @PostMapping("")
    public ResponseEntity<Map<String, Object>> addAdmin(@RequestBody Map<String, Object> requestBody) {
        Map<String, Object> responseBody = new HashMap<>();

        /* request body validation : AdminModel.json */
        Map<String, String> validateResultMap = validator.validate("AdminModel.json", requestBody);
        if (validateResultMap.get("status").equals("fail") || validateResultMap.get("status").equals("error")) {
            return validator.formatResponseBody(validateResultMap);
        }

        /* additional field */
        if (requestBody.get("adminId") == null) {
            responseBody.put("error_code", ErrorCode.POST_INVALID_PARAM);
            responseBody.put("reason", "The admin id is a mandatory field.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        if (requestBody.get("adminPassword") == null) {
            responseBody.put("error_code", ErrorCode.POST_INVALID_PARAM);
            responseBody.put("reason", "The admin password is a mandatory field.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        /* adminId already existence check */
        if (dataExistenceValidator.isAdminExist((String) requestBody.get("adminId"))) {
            responseBody.put("error_code", ErrorCode.POST_INVALID_PARAM);
            responseBody.put("reason", "The given admin id already exists.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        /* service call */
        Map<String, Object> resultMap = adminService.addAdmin(requestBody);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @PutMapping("/{adminId}")
    public ResponseEntity<Map<String, Object>> editAdmin(@PathVariable String adminId,
                                                         @RequestBody Map<String, Object> requestBody) {
        Map<String, Object> responseBody = new HashMap<>();

        /* check path variable */
        if (!dataExistenceValidator.isAdminExist(adminId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

        /* request body validation : AdminModel.json */
        Map<String, String> validateResultMap = validator.validate("AdminModel.json", requestBody);
        if (validateResultMap.get("status").equals("fail") || validateResultMap.get("status").equals("error")) {
            return validator.formatResponseBody(validateResultMap);
        }

        /* service call */
        Map<String, Object> resultMap = adminService.editAdmin(adminId, requestBody);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @DeleteMapping("/{adminId}")
    public ResponseEntity<Map<String, Object>> deleteAdmin(@PathVariable String adminId) {
        Map<String, Object> responseBody = new HashMap<>();

        /* check path variable */
        if (!dataExistenceValidator.isAdminExist(adminId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

        /* service call */
        Map<String, Object> resultMap = adminService.inactivateAdmin(adminId);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


}
