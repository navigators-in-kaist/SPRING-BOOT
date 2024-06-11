package com.navigators.demo.local.location.service.impl;

import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.global.dao.building.BuildingDao;
import com.navigators.demo.global.dao.location.LocationDao;
import com.navigators.demo.global.dao.locationCategory.LocationCategoryDao;
import com.navigators.demo.global.entity.Building;
import com.navigators.demo.global.entity.Location;
import com.navigators.demo.global.entity.LocationCategory;
import com.navigators.demo.local.location.service.LocationService;
import com.navigators.demo.util.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LocationServiceImpl implements LocationService {

    private final MapUtils mapUtils = new MapUtils();

    private final LocationDao locationDao;
    private final BuildingDao buildingDao;
    private final LocationCategoryDao locationCategoryDao;

    @Autowired
    public LocationServiceImpl(LocationDao locationDao,
                               BuildingDao buildingDao,
                               LocationCategoryDao locationCategoryDao) {
        this.locationDao = locationDao;
        this.buildingDao = buildingDao;
        this.locationCategoryDao = locationCategoryDao;
    }

    @Override
    public Map<String, Object> getLocationListByCategoryIdAndCurrentPosition(String categoryId, Double currLat, Double currLong) {
        Map<String, Object> resultMap = new HashMap<>();

        /* get location list of category */
        List<Location> locationList = locationDao.getLocationListByCategoryId(categoryId);

        /* additional information */
        List<Map<String, Object>> locationListAfter = new ArrayList<>();
        for (Location location : locationList) {
            Building targetBuilding = buildingDao.getEntityById(location.getLocationBuildingId()).get();
            LocationCategory targetLocationCategory = locationCategoryDao.getEntityById(location.getLocationCategoryId()).get();
            Double locationDistance = mapUtils.haversine(currLat, currLong, targetBuilding.getLatitude(), targetBuilding.getLongitude());
            Map<String, Object> newInfoMap = location.toDto().toMap(
                    locationDistance,
                    targetLocationCategory.getCategoryName(),
                    targetBuilding.getBuildingName(),
                    targetBuilding.getOfficialCode(),
                    targetBuilding.getLongitude(),
                    targetBuilding.getLatitude()
            );
            locationListAfter.add(newInfoMap);
        }

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("locationList", locationListAfter);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

}
