package com.navigators.demo.global.dao.locationCategory.impl;

import com.navigators.demo.global.dao.locationCategory.LocationCategoryDao;
import com.navigators.demo.global.dto.LocationCategoryDto;
import com.navigators.demo.global.entity.LocationCategory;
import com.navigators.demo.global.repository.LocationCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class LocationCategoryDaoImpl implements LocationCategoryDao {

    private final LocationCategoryRepository locationCategoryRepository;

    @Autowired
    public LocationCategoryDaoImpl(LocationCategoryRepository locationCategoryRepository) {
        this.locationCategoryRepository = locationCategoryRepository;
    }

    @Override
    public Optional<LocationCategory> getEntityById(String categoryId) {
        return locationCategoryRepository.findById(categoryId);
    }

    @Override
    public List<LocationCategory> getLocationCategoryList() {
        return locationCategoryRepository.findAll();
    }

    @Override
    public void saveDto(LocationCategoryDto locationCategoryDto) {
        locationCategoryRepository.save(locationCategoryDto.toEntity());
    }

    @Override
    public void deleteById(String categoryId) {
        locationCategoryRepository.deleteById(categoryId);
    }
}
