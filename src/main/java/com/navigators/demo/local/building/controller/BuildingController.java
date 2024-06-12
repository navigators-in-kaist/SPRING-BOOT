package com.navigators.demo.local.building.controller;

import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.local.building.service.BuildingService;
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
@RequestMapping("/api/v1/building")
public class BuildingController {

    private final HeaderParser parser = new HeaderParser();
    private final JsonValidator validator = new JsonValidator();
    private final ResponseFormatter responseFormatter = new ResponseFormatter();

    private final DataExistenceValidator dataExistenceValidator;
    private final BuildingService buildingService;

    @Autowired
    public BuildingController(DataExistenceValidator dataExistenceValidator,
                              BuildingService buildingService) {
        this.dataExistenceValidator = dataExistenceValidator;
        this.buildingService = buildingService;
    }


    @GetMapping("/{buildingId}")
    public ResponseEntity<Map<String, Object>> getBuildingDetail(@RequestHeader Map<String, String> requestHeader,
                                                                 @PathVariable String buildingId) {
        Map<String, Object> responseBody = new HashMap<>();

        /* path variable check */
        if (!dataExistenceValidator.isBuildingExist(buildingId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

        String coverage = "";
        /* coverage */
        String roleName = parser.getRoleNameByAccessToken(requestHeader.get("authorization"));
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

        /* get userId */
        String userId = "";
        if (coverage.equals("user")) {
            userId = parser.getUserIdByAccessToken(requestHeader.get("authorization"));
            if (!dataExistenceValidator.isUserExist(userId)) {
                responseBody.put("error_code", ErrorCode.GET_INVALID_PARAM);
                responseBody.put("reason", "The given user does not exists.");
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }
        }

        /* service call */
        Map<String, Object> resultMap;
        if (coverage.equals("all")) {
            resultMap = buildingService.getBuildingDetailedInfo(coverage, null, buildingId);
        } else {
            resultMap = buildingService.getBuildingDetailedInfo(coverage, userId, buildingId);
        }
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }

}
