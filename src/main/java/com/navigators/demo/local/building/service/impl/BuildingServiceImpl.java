package com.navigators.demo.local.building.service.impl;

import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.global.dao.building.BuildingDao;
import com.navigators.demo.global.dao.image.ImageDao;
import com.navigators.demo.global.dao.location.LocationDao;
import com.navigators.demo.global.dao.searchHistory.SearchHistoryDao;
import com.navigators.demo.global.dao.user.UserDao;
import com.navigators.demo.global.dao.userSave.UserSaveDao;
import com.navigators.demo.global.dto.BuildingDto;
import com.navigators.demo.global.entity.Building;
import com.navigators.demo.global.entity.Image;
import com.navigators.demo.global.entity.Location;
import com.navigators.demo.global.entity.User;
import com.navigators.demo.local.building.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.*;

@Service
public class BuildingServiceImpl implements BuildingService {

    private final UserDao userDao;
    private final BuildingDao buildingDao;
    private final ImageDao imageDao;
    private final UserSaveDao userSaveDao;
    private final SearchHistoryDao searchHistoryDao;
    private final LocationDao locationDao;

    @Autowired
    public BuildingServiceImpl(UserDao userDao,
                               BuildingDao buildingDao,
                               ImageDao imageDao,
                               UserSaveDao userSaveDao,
                               SearchHistoryDao searchHistoryDao,
                               LocationDao locationDao) {
        this.userDao = userDao;
        this.buildingDao = buildingDao;
        this.imageDao = imageDao;
        this.userSaveDao = userSaveDao;
        this.searchHistoryDao = searchHistoryDao;
        this.locationDao = locationDao;
    }


    /* If user request, "isSaved" is included and search history is added as a side effect. */
    @Override
    public Map<String, Object> getBuildingDetailedInfo(String coverage, @Nullable String userId, String buildingId) {
        Map<String, Object> resultMap = new HashMap<>();

        /* get building information */
        Map<String, Object> infoMap = buildingDao.getEntityById(buildingId).get().toDto().toMap();

        /* get image list */
        List<Image> imageList = imageDao.getImageList("building", buildingId);
        infoMap.put("imageList", imageList);

        /** (additional) for user requests */
        if (coverage.equals("user") && (userId != null)) {
            /* target user */
            User targetUser = userDao.getUserEntityById(userId).get();

            /* get isSaved for the user */
            String pseudoId = targetUser.getUserUuid() + buildingId;
            infoMap.put("isSaved", userSaveDao.getEntityById(pseudoId).isPresent());

            /* add or update search history */
            searchHistoryDao.addOrUpdatedEntity(targetUser.getUserUuid(), "building", buildingId);
        }

        /* no error */
        resultMap.put("item", infoMap);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }


    @Override
    public Map<String, Object> getBuildingList() {
        Map<String, Object> resultMap = new HashMap<>();

        List<Building> buildingList = buildingDao.getAll();

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("buildingList", buildingList);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }


    @Override
    public Map<String, Object> addBuilding(Map<String, Object> requestBody) {
        Map<String, Object> resultMap = new HashMap<>();

        /* check duplication for the official code */
        if (buildingDao.checkOfficialCodeDuplication((String) requestBody.get("officialCode"))) {
            resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
            resultMap.put("reason", "The official code of the building is already existing.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /* craft a new dto */
        BuildingDto buildingDto = BuildingDto.builder()
                .buildingId(UUID.randomUUID().toString())
                .officialCode((String) requestBody.get("officialCode"))
                .buildingName((String) requestBody.get("buildingName"))
                .buildingAlias((String) requestBody.get("buildingAlias"))
                .description((String) requestBody.get("description"))
                .maxFloor(Integer.parseInt(requestBody.get("maxFloor").toString()))
                .latitude(Double.parseDouble(requestBody.get("latitude").toString()))
                .longitude(Double.parseDouble(requestBody.get("longitude").toString()))
                .build();

        /* save */
        buildingDao.saveDto(buildingDto);

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }


    @Override
    public Map<String, Object> editBuilding(String buildingId, Map<String, Object> requestBody) {
        Map<String, Object> resultMap = new HashMap<>();

        /* check duplication for the official code */
        if (buildingDao.checkOfficialCodeDuplicationExceptForSelf(buildingId, (String) requestBody.get("officialCode"))) {
            resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
            resultMap.put("reason", "The official code of the building is already existing.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /* check floors in the building */
        List<Location> locationList = locationDao.getLocationListByBuildingId(buildingId);
        Integer newMaxFloor = Integer.parseInt(requestBody.get("maxFloor").toString());
        for (Location location : locationList) {
            if (location.getLocationFloor() > newMaxFloor) {
                resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
                resultMap.put("reason", "There exists a location in " + location.getLocationFloor() + " floor of the building.");
                resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
                return resultMap;
            }
        }

        /* get target entity */
        BuildingDto buildingDto = buildingDao.getEntityById(buildingId).get().toDto();

        /* set value */
        buildingDto.setOfficialCode((String) requestBody.get("officialCode"));
        buildingDto.setBuildingName((String) requestBody.get("buildingName"));
        buildingDto.setBuildingAlias((String) requestBody.get("buildingAlias"));
        buildingDto.setDescription((String) requestBody.get("description"));
        buildingDto.setMaxFloor(Integer.parseInt(requestBody.get("maxFloor").toString()));
        buildingDto.setLatitude(Double.parseDouble(requestBody.get("latitude").toString()));
        buildingDto.setLongitude(Double.parseDouble(requestBody.get("longitude").toString()));

        /* save */
        buildingDao.saveDto(buildingDto);

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

}
