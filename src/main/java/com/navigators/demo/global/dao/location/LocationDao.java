package com.navigators.demo.global.dao.location;

import com.navigators.demo.global.entity.Location;

import java.util.List;
import java.util.Optional;

public interface LocationDao {

    Optional<Location> getEntityById(String locationId);

    List<Location> getLocationListByCategoryId(String categoryId);
    List<Location> searchLocationByPayload(String payload);

    Integer getCountByCategoryId(String categoryId);

}
