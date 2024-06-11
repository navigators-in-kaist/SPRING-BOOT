package com.navigators.demo.global.repository;

import com.navigators.demo.global.entity.LocationCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationCategoryRepository extends JpaRepository<LocationCategory, String> {

    Optional<LocationCategory> findById(String categoryId);

}
