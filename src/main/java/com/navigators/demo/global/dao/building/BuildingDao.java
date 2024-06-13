package com.navigators.demo.global.dao.building;

import com.navigators.demo.global.dto.BuildingDto;
import com.navigators.demo.global.entity.Building;

import java.util.List;
import java.util.Optional;

public interface BuildingDao {

    Optional<Building> getEntityById(String buildingId);

    boolean checkOfficialCodeDuplication(String payload);
    boolean checkOfficialCodeDuplicationExceptForSelf(String buildingId, String payload);

    List<Building> searchByPayload(String payload);
    List<Building> getAll();

    void saveDto(BuildingDto buildingDto);
}
