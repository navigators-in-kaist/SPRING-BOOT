package com.navigators.demo.local.location.controller;

import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.local.location.service.LocationService;
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
@RequestMapping("/api/v1/location")
public class LocationController {

    private final HeaderParser parser = new HeaderParser();
    private final JsonValidator validator = new JsonValidator();
    private final ResponseFormatter responseFormatter = new ResponseFormatter();

    private final DataExistenceValidator dataExistenceValidator;
    private final LocationService locationService;

    @Autowired
    public LocationController(DataExistenceValidator dataExistenceValidator,
                              LocationService locationService) {
        this.dataExistenceValidator = dataExistenceValidator;
        this.locationService = locationService;
    }


    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getLocationListWithCategory(@PathVariable String categoryId,
                                                                           @RequestParam String latitude,
                                                                           @RequestParam String longitude) {
        Map<String, Object> responseBody = new HashMap<>();

        /* check path variable */
        if (!dataExistenceValidator.isLocationCategoryExist(categoryId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

        /* parse lat and long */
        Double latitudeValue = 0.0;
        Double longitudeValue = 0.0;
        try {
            latitudeValue = Double.parseDouble(latitude);
            longitudeValue = Double.parseDouble(longitude);
        } catch (Exception e) {
            responseBody.put("error_code", ErrorCode.GET_INVALID_PARAM);
            responseBody.put("reason", "The given latitude or longitude have an invalid format (parsing failed). They are in double type.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        /* range */
        if (latitudeValue < -90.0 || latitudeValue > 90.0) {
            responseBody.put("error_code", ErrorCode.GET_INVALID_PARAM);
            responseBody.put("reason", "The latitude must be in scope -90.0 ~ 90.0.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        if (longitudeValue < 0.0 || longitudeValue > 180.0) {
            responseBody.put("error_code", ErrorCode.GET_INVALID_PARAM);
            responseBody.put("reason", "The longitude must be in scope 0.0 ~ 180.0.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        /* service call */
        Map<String, Object> resultMap = locationService.getLocationListByCategoryIdAndCurrentPosition(categoryId, latitudeValue, longitudeValue);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @GetMapping("/{locationId}")
    public ResponseEntity<Map<String, Object>> getLocationDetail(@RequestHeader Map<String, String> requestHeader,
                                                                 @PathVariable String locationId) {
        Map<String, Object> responseBody = new HashMap<>();

        /* path variable check */
        if (!dataExistenceValidator.isLocationExist(locationId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
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

        /* get userId */
        String userId = "";
        if (coverage.equals("user")) {
            userId = parser.getUserIdByAccessToken(requestHeader.get("kauthorization"));
            if (!dataExistenceValidator.isUserExist(userId)) {
                responseBody.put("error_code", ErrorCode.GET_INVALID_PARAM);
                responseBody.put("reason", "The given user does not exists.");
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }
        }

        /* service call */
        Map<String, Object> resultMap;
        if (coverage.equals("all")) {
            resultMap = locationService.getLocationDetailedInfo(coverage, null, locationId);
        } else {
            resultMap = locationService.getLocationDetailedInfo(coverage, userId, locationId);
        }
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @PostMapping("")
    public ResponseEntity<Map<String, Object>> addLocation(@RequestBody Map<String, Object> requestBody) {
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

        /* service call */
        Map<String, Object> resultMap = locationService.addLocation(requestBody);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @PutMapping("/{locationId}")
    public ResponseEntity<Map<String, Object>> editLocation(@PathVariable String locationId,
                                                            @RequestBody Map<String, Object> requestBody) {
        Map<String, Object> responseBody = new HashMap<>();

        /* path variable check */
        if (!dataExistenceValidator.isLocationExist(locationId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

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

        /* service call */
        Map<String, Object> resultMap = locationService.editLocation(locationId, requestBody);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @DeleteMapping("/{locationId}")
    public ResponseEntity<Map<String, Object>> deleteLocation(@PathVariable String locationId) {
        Map<String, Object> responseBody = new HashMap<>();

        /* path variable check */
        if (!dataExistenceValidator.isLocationExist(locationId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

        /* service call */
        Map<String, Object> resultMap = locationService.deleteLocation(locationId);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }

}
