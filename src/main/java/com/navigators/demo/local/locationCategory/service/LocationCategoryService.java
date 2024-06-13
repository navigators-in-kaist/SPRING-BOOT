package com.navigators.demo.local.locationCategory.service;

import java.util.Map;

public interface LocationCategoryService {

    Map<String, Object> getLocationCategoryList();
    Map<String, Object> addLocationCategory(Map<String, Object> requestBody);
    Map<String, Object> editLocationCategory(String categoryId, Map<String, Object> requestBody);
    Map<String, Object> deleteLocationCategory(String categoryId);

}
