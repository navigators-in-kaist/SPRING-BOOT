package com.navigators.demo.global.dao.locationCategory;

import com.navigators.demo.global.entity.LocationCategory;

import java.util.List;
import java.util.Optional;

public interface LocationCategoryDao {

    Optional<LocationCategory> getEntityById(String categoryId);

    List<LocationCategory> getLocationCategoryList();

}
