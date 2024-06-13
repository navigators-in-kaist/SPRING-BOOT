package com.navigators.demo.local.locationCategory.service.impl;

import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.global.dao.location.LocationDao;
import com.navigators.demo.global.dao.locationCategory.LocationCategoryDao;
import com.navigators.demo.global.dto.LocationCategoryDto;
import com.navigators.demo.global.entity.Location;
import com.navigators.demo.global.entity.LocationCategory;
import com.navigators.demo.local.locationCategory.service.LocationCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Override
    public Map<String, Object> addLocationCategory(Map<String, Object> requestBody) {
        Map<String, Object> resultMap = new HashMap<>();

        LocationCategoryDto locationCategoryDto = LocationCategoryDto.builder()
                .categoryId(UUID.randomUUID().toString())
                .categoryName((String) requestBody.get("categoryName"))
                .description((String) requestBody.get("description"))
                .build();

        locationCategoryDao.saveDto(locationCategoryDto);

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

    @Override
    public Map<String, Object> editLocationCategory(String categoryId, Map<String, Object> requestBody) {
        Map<String, Object> resultMap = new HashMap<>();

        LocationCategoryDto locationCategoryDto = locationCategoryDao.getEntityById(categoryId).get().toDto();

        locationCategoryDto.setCategoryName((String) requestBody.get("categoryName"));
        locationCategoryDto.setDescription((String) requestBody.get("description"));

        locationCategoryDao.saveDto(locationCategoryDto);

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

    @Override
    public Map<String, Object> deleteLocationCategory(String categoryId) {
        Map<String, Object> resultMap = new HashMap<>();

        /* check location */
        Integer count = locationDao.getCountByCategoryId(categoryId);
        if (count != 0) {
            resultMap.put("errorCode", ErrorCode.DELETE_INVALID_PARAM);
            resultMap.put("reason", "There exist a location in the category");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        locationCategoryDao.deleteById(categoryId);

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

}
