package com.navigators.demo.local.locationCategory.service.impl;

import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.global.dao.location.LocationDao;
import com.navigators.demo.global.dao.locationCategory.LocationCategoryDao;
import com.navigators.demo.global.entity.LocationCategory;
import com.navigators.demo.local.locationCategory.service.LocationCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LocationCategoryServiceImpl implements LocationCategoryService {

    private final LocationDao locationDao;
    private final LocationCategoryDao locationCategoryDao;

    @Autowired
    public LocationCategoryServiceImpl(LocationDao locationDao,
                                       LocationCategoryDao locationCategoryDao) {
        this.locationDao = locationDao;
        this.locationCategoryDao = locationCategoryDao;
    }

    @Override
    public Map<String, Object> getLocationCategoryList() {
        Map<String, Object> resultMap = new HashMap<>();

        /* get location category list */
        List<LocationCategory> locationCategoryList = locationCategoryDao.getLocationCategoryList();

        /* additional information */
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (LocationCategory locationCategory : locationCategoryList) {
            Integer locationCount = locationDao.getCountByCategoryId(locationCategory.getCategoryId());
            resultList.add(locationCategory.toDto().toMap(locationCount));
        }

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("locationCategoryList", resultList);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

}
