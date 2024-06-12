package com.navigators.demo.local.location.service;

import javax.annotation.Nullable;
import java.util.Map;

public interface LocationService {

    Map<String, Object> getLocationListByCategoryIdAndCurrentPosition(String categoryId, Double currLat, Double currLong);
    Map<String, Object> getLocationDetailedInfo(String coverage, @Nullable String userId, String locationId);

}
