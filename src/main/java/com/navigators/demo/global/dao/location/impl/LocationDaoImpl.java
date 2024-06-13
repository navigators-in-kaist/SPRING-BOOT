package com.navigators.demo.global.dao.location.impl;

import com.navigators.demo.global.dao.location.LocationDao;
import com.navigators.demo.global.entity.Location;
import com.navigators.demo.global.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class LocationDaoImpl implements LocationDao {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationDaoImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Optional<Location> getEntityById(String locationId) {
        return locationRepository.findById(locationId);
    }

    @Override
    public List<Location> getLocationListByCategoryId(String categoryId) {
        return locationRepository.findByLocationCategoryId(categoryId);
    }

    @Override
    public List<Location> searchLocationByPayload(String payload) {
        return locationRepository.findByLocationNameContainingOrRoomNumberContaining(payload, payload);
    }

    @Override
    public Integer getCountByCategoryId(String categoryId) {
        return locationRepository.findByLocationCategoryId(categoryId).size();
    }

    @Override
    public List<Location> getLocationListByBuildingId(String buildingId) {
        return locationRepository.findByLocationBuildingId(buildingId);
    }

}
