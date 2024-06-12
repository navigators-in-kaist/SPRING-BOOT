package com.navigators.demo.local.building.service;

import javax.annotation.Nullable;
import java.util.Map;

public interface BuildingService {

    Map<String, Object> getBuildingDetailedInfo(String coverage, @Nullable String userId, String buildingId);

}
