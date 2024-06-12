package com.navigators.demo.local.search.service.impl;

import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.global.dao.building.BuildingDao;
import com.navigators.demo.global.dao.location.LocationDao;
import com.navigators.demo.global.dao.searchHistory.SearchHistoryDao;
import com.navigators.demo.global.dao.user.UserDao;
import com.navigators.demo.global.entity.Building;
import com.navigators.demo.global.entity.Location;
import com.navigators.demo.global.entity.SearchHistory;
import com.navigators.demo.global.entity.User;
import com.navigators.demo.local.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {

    private final SearchHistoryDao searchHistoryDao;
    private final UserDao userDao;
    private final BuildingDao buildingDao;
    private final LocationDao locationDao;

    @Autowired
    public SearchServiceImpl(SearchHistoryDao searchHistoryDao,
                             UserDao userDao,
                             BuildingDao buildingDao,
                             LocationDao locationDao) {
        this.searchHistoryDao = searchHistoryDao;
        this.userDao = userDao;
        this.buildingDao = buildingDao;
        this.locationDao = locationDao;
    }

    @Override
    public Map<String, Object> getSearchHistoryList(String userId) {
        Map<String, Object> resultMap = new HashMap<>();

        /* get target user */
        User targetUser = userDao.getUserEntityById(userId).get();

        /* get history list */
        List<SearchHistory> searchHistories = searchHistoryDao.getSearchHistoryByUserUuid(targetUser.getUserUuid());

        /* data format */
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (SearchHistory searchHistory : searchHistories) {
            if (searchHistory.getSearchHistoryBuildingId() != null) {
                /* target building */
                Building targetBuilding = buildingDao.getEntityById(searchHistory.getSearchHistoryBuildingId()).get();
                Map<String, Object> infoMap = searchHistory.toDto().toMap(
                        targetBuilding.getOfficialCode(),
                        targetBuilding.getBuildingName()
                );
                infoMap.put("type", "building");
                resultList.add(infoMap);
            } else { /* location */
                /* target location */
                Location targetLocation = locationDao.getEntityById(searchHistory.getSearchHistoryLocationId()).get();
                /* target building */
                Building targetBuilding = buildingDao.getEntityById(targetLocation.getLocationBuildingId()).get();
                Map<String, Object> infoMap = searchHistory.toDto().toMap(
                        targetBuilding.getOfficialCode(),
                        targetLocation.getLocationName()
                );
                infoMap.put("type", "location");
                resultList.add(infoMap);
            }
        }

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("histories", resultList);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

    @Override
    public Map<String, Object> deleteSearchHistory(String userId, String historyId) {
        Map<String, Object> resultMap = new HashMap<>();

        /* get target user */
        User targetUser = userDao.getUserEntityById(userId).get();

        /* get target entity */
        SearchHistory targetHistory = searchHistoryDao.getEntityById(historyId).get();

        /* value combination check */
        if (!targetHistory.getSearchHistoryUserId().equals(targetUser.getUserUuid())) {
            resultMap.put("errorCode", ErrorCode.DELETE_INVALID_PARAM);
            resultMap.put("reason", "The given search history is not of the given user.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /* delete */
        searchHistoryDao.deleteDto(targetHistory.toDto());

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }
}
