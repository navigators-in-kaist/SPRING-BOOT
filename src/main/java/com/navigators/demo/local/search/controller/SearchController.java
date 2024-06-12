package com.navigators.demo.local.search.controller;

import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.local.search.service.SearchService;
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
@RequestMapping("/api/v1/search")
public class SearchController {

    private final HeaderParser parser = new HeaderParser();
    private final JsonValidator validator = new JsonValidator();
    private final ResponseFormatter responseFormatter = new ResponseFormatter();

    private final DataExistenceValidator dataExistenceValidator;
    private final SearchService searchService;

    @Autowired
    public SearchController(DataExistenceValidator dataExistenceValidator,
                            SearchService searchService) {
        this.dataExistenceValidator = dataExistenceValidator;
        this.searchService = searchService;
    }


    /** Note. The payload will match with following fields
     *  1.  building name
     *  2.  building alias
     *  3.  building official code
     *  4.  location name
     *  5.  location room number
     * */
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> searchAnything(@RequestParam String payload) {
        Map<String, Object> responseBody = new HashMap<>();

        /* service call */
        Map<String, Object> resultMap = searchService.searchAnything(payload);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getSearchHistory(@RequestHeader Map<String, String> requestHeader) {
        Map<String, Object> responseBody = new HashMap<>();

        /* get userId */
        String userId = parser.getUserIdByAccessToken(requestHeader.get("kauthorization"));
        if (!dataExistenceValidator.isUserExist(userId)) {
            responseBody.put("error_code", ErrorCode.GET_INVALID_PARAM);
            responseBody.put("reason", "The given user does not exists.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        /* service call */
        Map<String, Object> resultMap = searchService.getSearchHistoryList(userId);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }


    @DeleteMapping("/history/{historyId}")
    public ResponseEntity<Map<String, Object>> deleteSearchHistory(@RequestHeader Map<String, String> requestHeader,
                                                                   @PathVariable String historyId) {
        Map<String, Object> responseBody = new HashMap<>();

        /* check path variable */
        if (!dataExistenceValidator.isSearchHistoryExist(historyId)) {
            responseBody.put("error_code", ErrorCode.RESOURCE_NOT_EXIST);
            responseBody.put("reason", "There is no resource for the path.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

        /* get userId */
        String userId = parser.getUserIdByAccessToken(requestHeader.get("kauthorization"));
        if (!dataExistenceValidator.isUserExist(userId)) {
            responseBody.put("error_code", ErrorCode.GET_INVALID_PARAM);
            responseBody.put("reason", "The given user does not exists.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        /* service call */
        Map<String, Object> resultMap = searchService.deleteSearchHistory(userId, historyId);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }

}
