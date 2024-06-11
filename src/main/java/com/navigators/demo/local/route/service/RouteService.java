package com.navigators.demo.local.route.service;

import java.util.Map;

public interface RouteService {

    Map<String, Object> findRoute(Double startLat, Double startLon, Double endLat, Double endLon);

}
