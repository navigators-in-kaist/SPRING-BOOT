package com.navigators.demo.global.dao.location;

import com.navigators.demo.global.dto.LocationDto;
import com.navigators.demo.global.entity.Location;

import java.util.List;
import java.util.Optional;

public interface LocationDao {

    Optional<Location> getEntityById(String locationId);

    List<Location> getLocationListByCategoryId(String categoryId);
    List<Location> searchLocationByPayload(String payload);
    List<Location> getLocationListByBuildingId(String buildingId);

    Integer getCountByCategoryId(String categoryId);

    void saveDto(LocationDto locationDto);
    void deleteDto(LocationDto locationDto);

}
