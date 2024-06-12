package com.navigators.demo.local.locationCategory.controller;

import com.navigators.demo.local.locationCategory.service.LocationCategoryService;
import com.navigators.demo.util.HeaderParser;
import com.navigators.demo.util.ResponseFormatter;
import com.navigators.demo.validator.DataExistenceValidator;
import com.navigators.demo.validator.JsonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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




}
