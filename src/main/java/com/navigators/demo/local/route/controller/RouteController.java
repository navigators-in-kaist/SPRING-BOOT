package com.navigators.demo.local.route.controller;

import com.navigators.demo.local.route.service.RouteService;
import com.navigators.demo.util.ResponseFormatter;
import com.navigators.demo.validator.DataExistenceValidator;
import com.navigators.demo.validator.JsonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/route")
public class RouteController {

    private final JsonValidator validator = new JsonValidator();
    private final ResponseFormatter responseFormatter = new ResponseFormatter();

    private final DataExistenceValidator dataExistenceValidator;
    private final RouteService routeService;

    @Autowired
    public RouteController(DataExistenceValidator dataExistenceValidator,
                           RouteService routeService) {
        this.dataExistenceValidator = dataExistenceValidator;
        this.routeService = routeService;
    }

    @PostMapping("")
    @RolesAllowed({ "proven_user", "unproven_user" })
    public ResponseEntity<Map<String, Object>> getRoute(@RequestBody Map<String, Object> requestBody) {
        Map<String, Object> responseBody = new HashMap<>();

        /* request body validation : RouteRequestModel.json */
        Map<String, String> validateResultMap = validator.validate("RouteRequestModel.json", requestBody);
        if (validateResultMap.get("status").equals("fail") || validateResultMap.get("status").equals("error")) {
            return validator.formatResponseBody(validateResultMap);
        }

        /* parse values */
        Double startLat = Double.parseDouble(requestBody.get("fromLatitude").toString());
        Double startLon = Double.parseDouble(requestBody.get("fromLongitude").toString());
        Double endLat = Double.parseDouble(requestBody.get("toLatitude").toString());
        Double endLon = Double.parseDouble(requestBody.get("toLongitude").toString());

        /* service call */
        Map<String, Object> resultMap = routeService.findRoute(startLat, startLon, endLat, endLon);
        return responseFormatter.getMapResponseEntity(responseBody, resultMap);
    }

}
