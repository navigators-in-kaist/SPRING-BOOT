package com.navigators.demo.local.route.service.impl;

import com.navigators.demo.adapter.api.tmap.TMapAPI;
import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.local.route.service.RouteService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RouteServiceImpl implements RouteService {

    private final TMapAPI _api = new TMapAPI();

    @Override
    public Map<String, Object> findRoute(Double startLat, Double startLon, Double endLat, Double endLon) {
        Map<String, Object> resultMap = new HashMap<>();

        Map<String, Object> routeResultMap = _api.getRoute(startLat, startLon, endLat, endLon);

        if (routeResultMap.get("result").equals("fail")) {
            resultMap.put("errorCode", ErrorCode.EXTERNAL_API_ERROR);
            resultMap.put("reason", routeResultMap.get("reason"));
            resultMap.put("httpStatusCode", HttpStatus.INTERNAL_SERVER_ERROR);
            return resultMap;
        } else {
            /* no error */
            resultMap.put("item", routeResultMap.get("item"));
            resultMap.put("errorCode", ErrorCode.NO_ERROR);
            resultMap.put("httpStatusCode", HttpStatus.OK);
            return resultMap;
        }
    }

}
