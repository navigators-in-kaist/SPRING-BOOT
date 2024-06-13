package com.navigators.demo.local.contribution.controller;

import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.local.contribution.service.ContributionService;
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
@RequestMapping("/api/v1/contribution")
public class ContributionController {

    private final HeaderParser parser = new HeaderParser();
    private final JsonValidator validator = new JsonValidator();
    private final ResponseFormatter responseFormatter = new ResponseFormatter();

    private final DataExistenceValidator dataExistenceValidator;
    private final ContributionService contributionService;

    @Autowired
    public ContributionController(DataExistenceValidator dataExistenceValidator,
                                  ContributionService contributionService) {
        this.dataExistenceValidator = dataExistenceValidator;
        this.contributionService = contributionService;
    }

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getContributionList(@RequestHeader Map<String, String> requestHeader) {
        Map<String, Object> responseBody = new HashMap<>();

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
            coverage = "self";
        }

        /* get userId */
        String userId = "";
        if (coverage.equals("self")) {
            userId = parser.getUserIdByAccessToken(requestHeader.get("kauthorization"));
            if (!dataExistenceValidator.isUserExist(userId)) {
                responseBody.put("error_code", ErrorCode.GET_INVALID_PARAM);
                responseBody.put("reason", "The given user does not exists.");
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }
        }

        /* service call */
        Map<String, Object> resultMap;
        if (coverage.equals("self")) {
            resultMap = contributionService.getContributionListByUserId(userId);
        } else {
            resultMap = contributionService.getAllContributionList();
        }
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @DeleteMapping("/{contributionId}")
    public ResponseEntity<Map<String, Object>> deleteContribution(@RequestHeader Map<String, String> requestHeader,
                                                                  @PathVariable String contributionId) {
        Map<String, Object> responseBody = new HashMap<>();

        /* path variable check */
        if (!dataExistenceValidator.isContributionExist(contributionId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

        /* get userId */
        String userId = "";
        userId = parser.getUserIdByAccessToken(requestHeader.get("kauthorization"));
        if (!dataExistenceValidator.isUserExist(userId)) {
            responseBody.put("error_code", ErrorCode.GET_INVALID_PARAM);
            responseBody.put("reason", "The given user does not exists.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        /* service call */
        Map<String, Object> resultMap = contributionService.deleteContribution(userId, contributionId);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @PostMapping("/building")
    public ResponseEntity<Map<String, Object>> addBuildingContribution(@RequestHeader Map<String, String> requestHeader,
                                                                       @RequestBody Map<String, Object> requestBody) {
        Map<String, Object> responseBody = new HashMap<>();

        /* request body validation : BuildingModel.json */
        Map<String, String> validateResultMap = validator.validate("BuildingModel.json", requestBody);
        if (validateResultMap.get("status").equals("fail") || validateResultMap.get("status").equals("error")) {
            return validator.formatResponseBody(validateResultMap);
        }

        /* get userId */
        String userId = "";
        userId = parser.getUserIdByAccessToken(requestHeader.get("kauthorization"));
        if (!dataExistenceValidator.isUserExist(userId)) {
            responseBody.put("error_code", ErrorCode.GET_INVALID_PARAM);
            responseBody.put("reason", "The given user does not exists.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        /* service call */
        Map<String, Object> resultMap = contributionService.addBuildingContribution(userId, requestBody);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @GetMapping("/building/official-code")
    public ResponseEntity<Map<String, Object>> checkOfficialCodeDuplication(@RequestParam String payload) {
        Map<String, Object> responseBody = new HashMap<>();

        /* service call */
        Map<String, Object> resultMap = contributionService.checkOfficialCodeDuplication(payload);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @PostMapping("/location")
    public ResponseEntity<Map<String, Object>> addLocationContribution(@RequestHeader Map<String, String> requestHeader,
                                                                       @RequestBody Map<String, Object> requestBody) {
        Map<String, Object> responseBody = new HashMap<>();

        /* request body validation : LocationModel.json */
        Map<String, String> validateResultMap = validator.validate("LocationModel.json", requestBody);
        if (validateResultMap.get("status").equals("fail") || validateResultMap.get("status").equals("error")) {
            return validator.formatResponseBody(validateResultMap);
        }

        /* request body parameter existence */
        /* locationCategoryId */
        if (!dataExistenceValidator.isLocationCategoryExist((String) requestBody.get("locationCategoryId"))) {
            responseBody.put("error_code", ErrorCode.POST_INVALID_PARAM);
            responseBody.put("reason", "The given \"locationCategoryId\" does not exists.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        /* locationBuildingId */
        if (!dataExistenceValidator.isBuildingExist((String) requestBody.get("locationBuildingId"))) {
            responseBody.put("error_code", ErrorCode.POST_INVALID_PARAM);
            responseBody.put("reason", "The given \"locationBuildingId\" does not exists.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        /* get userId */
        String userId = "";
        userId = parser.getUserIdByAccessToken(requestHeader.get("kauthorization"));
        if (!dataExistenceValidator.isUserExist(userId)) {
            responseBody.put("error_code", ErrorCode.GET_INVALID_PARAM);
            responseBody.put("reason", "The given user does not exists.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        /* service call */
        Map<String, Object> resultMap = contributionService.addLocationContribution(userId, requestBody);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @PostMapping("/{contributionId}/approve")
    public ResponseEntity<Map<String, Object>> approveContribution(@PathVariable String contributionId) {
        Map<String, Object> responseBody = new HashMap<>();

        /* path variable check */
        if (!dataExistenceValidator.isContributionExist(contributionId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

        /* service call */
        Map<String, Object> resultMap = contributionService.approveContribution(contributionId);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @PostMapping("/{contributionId}/reject")
    public ResponseEntity<Map<String, Object>> rejectContribution(@PathVariable String contributionId) {
        Map<String, Object> responseBody = new HashMap<>();

        /* path variable check */
        if (!dataExistenceValidator.isContributionExist(contributionId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

        /* service call */
        Map<String, Object> resultMap = contributionService.rejectContribution(contributionId);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }
}
