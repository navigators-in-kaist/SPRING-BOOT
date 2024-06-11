package com.navigators.demo.global.repository;

import com.navigators.demo.global.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuildingRepository extends JpaRepository<Building, String> {

    Optional<Building> findById(String buildingId);

    List<Building> findByOfficialCode(String officialCode);

}
