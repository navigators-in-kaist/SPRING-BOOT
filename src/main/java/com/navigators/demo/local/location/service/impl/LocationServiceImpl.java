package com.navigators.demo.local.location.service.impl;

import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.global.dao.building.BuildingDao;
import com.navigators.demo.global.dao.image.ImageDao;
import com.navigators.demo.global.dao.location.LocationDao;
import com.navigators.demo.global.dao.locationCategory.LocationCategoryDao;
import com.navigators.demo.global.dao.searchHistory.SearchHistoryDao;
import com.navigators.demo.global.dao.user.UserDao;
import com.navigators.demo.global.dao.userSave.UserSaveDao;
import com.navigators.demo.global.dto.LocationDto;
import com.navigators.demo.global.entity.*;
import com.navigators.demo.local.location.service.LocationService;
import com.navigators.demo.util.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.*;

@Service
public class LocationServiceImpl implements LocationService {

    private final MapUtils mapUtils = new MapUtils();

    private final LocationDao locationDao;
    private final BuildingDao buildingDao;
    private final LocationCategoryDao locationCategoryDao;
    private final ImageDao imageDao;
    private final UserDao userDao;
    private final SearchHistoryDao searchHistoryDao;
    private final UserSaveDao userSaveDao;

    @Autowired
    public LocationServiceImpl(LocationDao locationDao,
                               BuildingDao buildingDao,
                               LocationCategoryDao locationCategoryDao,
                               ImageDao imageDao,
                               UserDao userDao,
                               SearchHistoryDao searchHistoryDao,
                               UserSaveDao userSaveDao) {
        this.locationDao = locationDao;
        this.buildingDao = buildingDao;
        this.locationCategoryDao = locationCategoryDao;
        this.imageDao = imageDao;
        this.userDao = userDao;
        this.searchHistoryDao = searchHistoryDao;
        this.userSaveDao = userSaveDao;
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

    @Override
    public Map<String, Object> getLocationDetailedInfo(String coverage, @Nullable String userId, String locationId) {
        Map<String, Object> resultMap = new HashMap<>();

        /* get location information */
        Location targetLocation = locationDao.getEntityById(locationId).get();

        /* get additional info */
        Building targetBuilding = buildingDao.getEntityById(targetLocation.getLocationBuildingId()).get();
        LocationCategory targetLocationCategory = locationCategoryDao.getEntityById(targetLocation.getLocationCategoryId()).get();
        Map<String, Object> newInfoMap = targetLocation.toDto().toMap(
                0.0,
                targetLocationCategory.getCategoryName(),
                targetBuilding.getBuildingName(),
                targetBuilding.getOfficialCode(),
                targetBuilding.getLongitude(),
                targetBuilding.getLatitude()
        );
        newInfoMap.remove("distanceFromCurrentPosition");

        /* get image list */
        List<Image> imageList = imageDao.getImageList("location", locationId);
        newInfoMap.put("imageList", imageList);

        /** (additional) for user requests */
        if (coverage.equals("user") && (userId != null)) {
            /* target user */
            User targetUser = userDao.getUserEntityById(userId).get();

            /* get isSaved for the user */
            String pseudoId = targetUser.getUserUuid() + locationId;
            newInfoMap.put("isSaved", userSaveDao.getEntityById(pseudoId).isPresent());

            /* add or update search history */
            searchHistoryDao.addOrUpdatedEntity(targetUser.getUserUuid(), "location", locationId);
        }

        /* no error */
        resultMap.put("item", newInfoMap);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

    @Override
    public Map<String, Object> addLocation(Map<String, Object> requestBody) {
        Map<String, Object> resultMap = new HashMap<>();

        /* location floor validation */
        Building targetBuilding = buildingDao.getEntityById((String) requestBody.get("locationBuildingId")).get();
        if (Integer.parseInt(requestBody.get("locationFloor").toString()) > targetBuilding.getMaxFloor()) {
            resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
            resultMap.put("reason", "The given building has " + targetBuilding.getMaxFloor() + " floor at most.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /* craft a new dto */
        LocationDto locationDto = LocationDto.builder()
                .locationId(UUID.randomUUID().toString())
                .locationName((String) requestBody.get("locationName"))
                .locationFloor(Integer.parseInt(requestBody.get("locationFloor").toString()))
                .description((String) requestBody.get("description"))
                .locationCategoryId((String) requestBody.get("locationCategoryId"))
                .locationBuildingId((String) requestBody.get("locationBuildingId"))
                .build();

        /* save */
        locationDao.saveDto(locationDto);

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

    @Override
    public Map<String, Object> editLocation(String locationId, Map<String, Object> requestBody) {
        Map<String, Object> resultMap = new HashMap<>();

        /* location floor validation */
        Building targetBuilding = buildingDao.getEntityById((String) requestBody.get("locationBuildingId")).get();
        if (Integer.parseInt(requestBody.get("locationFloor").toString()) > targetBuilding.getMaxFloor()) {
            resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
            resultMap.put("reason", "The given building has " + targetBuilding.getMaxFloor() + " floor at most.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /* get target location */
        LocationDto locationDto = locationDao.getEntityById(locationId).get().toDto();

        locationDto.setLocationName((String) requestBody.get("locationName"));
        locationDto.setLocationFloor(Integer.parseInt(requestBody.get("locationFloor").toString()));
        locationDto.setDescription((String) requestBody.get("description"));
        locationDto.setLocationCategoryId((String) requestBody.get("locationCategoryId"));
        locationDto.setLocationBuildingId((String) requestBody.get("locationBuildingId"));

        /* save */
        locationDao.saveDto(locationDto);

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

    @Override
    public Map<String, Object> deleteLocation(String locationId) {
        Map<String, Object> resultMap = new HashMap<>();

        /* get target location */
        LocationDto locationDto = locationDao.getEntityById(locationId).get().toDto();

        /* delete user saves */
        List<UserSave> locationUserSaveList = userSaveDao.getSaveListByLocationId(locationDto.getLocationId());
        for (UserSave userSave : locationUserSaveList) {
            userSaveDao.deleteDto(userSave.toDto());
        }

        /* delete images */
        List<Image> locationImageList = imageDao.getImageList("location", locationDto.getLocationId());
        for (Image image : locationImageList) {
            imageDao.deleteDto(image.toDto());
        }

        /* delete search history */
        List<SearchHistory> locationSearchHistory = searchHistoryDao.getSearchHistoryByLocationId(locationDto.getLocationId());
        for (SearchHistory searchHistory : locationSearchHistory) {
            searchHistoryDao.deleteDto(searchHistory.toDto());
        }

        /* delete location */
        locationDao.deleteDto(locationDto);

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

}
