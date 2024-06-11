package com.navigators.demo.local.location.service;

import java.util.Map;

public interface LocationService {

    Map<String, Object> getLocationListByCategoryIdAndCurrentPosition(String categoryId, Double currLat, Double currLong);

}
