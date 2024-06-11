package com.navigators.demo.global.repository;

import com.navigators.demo.global.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, String> {

    Optional<Location> findById(String locationId);

    List<Location> findByLocationCategoryId(String categoryId);
}
