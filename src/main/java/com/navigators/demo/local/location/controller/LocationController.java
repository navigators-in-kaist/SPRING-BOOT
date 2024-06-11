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
    @RolesAllowed({ "proven_user", "unproven_user" })
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

}
