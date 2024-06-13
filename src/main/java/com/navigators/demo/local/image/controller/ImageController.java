package com.navigators.demo.local.image.controller;

import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.local.image.service.ImageService;
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
@RequestMapping("/api/v1/image")
public class ImageController {

    private final HeaderParser parser = new HeaderParser();
    private final JsonValidator validator = new JsonValidator();
    private final ResponseFormatter responseFormatter = new ResponseFormatter();

    private final DataExistenceValidator dataExistenceValidator;
    private final ImageService imageService;


    @Autowired
    public ImageController(DataExistenceValidator dataExistenceValidator,
                           ImageService imageService) {
        this.dataExistenceValidator = dataExistenceValidator;
        this.imageService = imageService;
    }


    @PostMapping("/building/{buildingId}")
    public ResponseEntity<Map<String, Object>> addBuildingImage(@PathVariable String buildingId,
                                                                @RequestBody Map<String, Object> requestBody) {

        Map<String, Object> responseBody = new HashMap<>();

        /* path variable check */
        if (!dataExistenceValidator.isBuildingExist(buildingId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

        /* request body validation : ImageModel.json */
        Map<String, String> validateResultMap = validator.validate("ImageModel.json", requestBody);
        if (validateResultMap.get("status").equals("fail") || validateResultMap.get("status").equals("error")) {
            return validator.formatResponseBody(validateResultMap);
        }

        /* service call */
        Map<String, Object> resultMap = imageService.addImage("building", buildingId, (String) requestBody.get("url"));
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }

    @PostMapping("/location/{locationId}")
    public ResponseEntity<Map<String, Object>> addLocationImage(@PathVariable String locationId,
                                                                @RequestBody Map<String, Object> requestBody) {

        Map<String, Object> responseBody = new HashMap<>();

        /* path variable check */
        if (!dataExistenceValidator.isLocationExist(locationId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

        /* request body validation : ImageModel.json */
        Map<String, String> validateResultMap = validator.validate("ImageModel.json", requestBody);
        if (validateResultMap.get("status").equals("fail") || validateResultMap.get("status").equals("error")) {
            return validator.formatResponseBody(validateResultMap);
        }

        /* service call */
        Map<String, Object> resultMap = imageService.addImage("location", locationId, (String) requestBody.get("url"));
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @DeleteMapping("/{imageId}")
    public ResponseEntity<Map<String, Object>> deleteImage(@PathVariable String imageId) {
        Map<String, Object> responseBody = new HashMap<>();

        /* path variable check */
        if (!dataExistenceValidator.isImageExist(imageId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

        /* service call */
        Map<String, Object> resultMap = imageService.deleteImage(imageId);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }

}
