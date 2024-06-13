package com.navigators.demo.local.locationCategory.controller;

import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.local.locationCategory.service.LocationCategoryService;
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
@RequestMapping("/api/v1/location-category")
public class LocationCategoryController {

    private final HeaderParser parser = new HeaderParser();
    private final JsonValidator validator = new JsonValidator();
    private final ResponseFormatter responseFormatter = new ResponseFormatter();

    private final DataExistenceValidator dataExistenceValidator;
    private final LocationCategoryService locationCategoryService;

    @Autowired
    public LocationCategoryController(DataExistenceValidator dataExistenceValidator,
                                      LocationCategoryService locationCategoryService) {
        this.dataExistenceValidator = dataExistenceValidator;
        this.locationCategoryService = locationCategoryService;
    }

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getLocationCategoryList() {
        Map<String, Object> responseBody = new HashMap<>();

        return responseFormatter.getMapResponseEntity(responseBody, locationCategoryService.getLocationCategoryList());
    }


    @PostMapping("")
    public ResponseEntity<Map<String, Object>> addLocationCategory(@RequestBody Map<String, Object> requestBody) {
        Map<String, Object> responseBody = new HashMap<>();

        /* request body validation : LocationCategoryModel.json */
        Map<String, String> validateResultMap = validator.validate("LocationCategoryModel.json", requestBody);
        if (validateResultMap.get("status").equals("fail") || validateResultMap.get("status").equals("error")) {
            return validator.formatResponseBody(validateResultMap);
        }

        /* service call */
        Map<String, Object> resultMap = locationCategoryService.addLocationCategory(requestBody);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @PutMapping("/{categoryId}")
    public ResponseEntity<Map<String, Object>> editLocationCategory(@PathVariable String categoryId,
                                                                    @RequestBody Map<String, Object> requestBody) {
        Map<String, Object> responseBody = new HashMap<>();

        /* path variable check */
        if (!dataExistenceValidator.isLocationCategoryExist(categoryId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

        /* request body validation : LocationCategoryModel.json */
        Map<String, String> validateResultMap = validator.validate("LocationCategoryModel.json", requestBody);
        if (validateResultMap.get("status").equals("fail") || validateResultMap.get("status").equals("error")) {
            return validator.formatResponseBody(validateResultMap);
        }

        /* service call */
        Map<String, Object> resultMap = locationCategoryService.editLocationCategory(categoryId, requestBody);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Map<String, Object>> deleteLocationCategory(@PathVariable String categoryId,
                                                                    @RequestBody Map<String, Object> requestBody) {
        Map<String, Object> responseBody = new HashMap<>();

        /* path variable check */
        if (!dataExistenceValidator.isLocationCategoryExist(categoryId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

        /* service call */
        Map<String, Object> resultMap = locationCategoryService.deleteLocationCategory(categoryId);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }

}
