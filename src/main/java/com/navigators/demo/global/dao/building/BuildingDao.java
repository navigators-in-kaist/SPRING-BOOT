package com.navigators.demo.global.dao.building;

import com.navigators.demo.global.entity.Building;

import java.util.Optional;

public interface BuildingDao {

    Optional<Building> getEntityById(String buildingId);

    boolean checkOfficialCodeDuplication(String payload);
    boolean checkOfficialCodeDuplicationExceptForSelf(String buildingId, String payload);

}
