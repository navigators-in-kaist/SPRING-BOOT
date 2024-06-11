package com.navigators.demo.util;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@NoArgsConstructor
public class ResponseFormatter {

    public static ResponseEntity<Map<String, Object>> getMapResponseEntity(Map<String, Object> responseBody, Map<String, Object> resultMap) {
        responseBody.put("error_code", resultMap.get("errorCode"));
        if (resultMap.get("httpStatusCode") != HttpStatus.OK) {
            responseBody.put("reason", resultMap.get("reason"));
        }
        if (resultMap.get("httpStatusCode") == HttpStatus.OK) {
            responseBody.put("item", resultMap.get("item"));
        }
        return new ResponseEntity<>(responseBody, (HttpStatus) resultMap.get("httpStatusCode"));
    }

}
