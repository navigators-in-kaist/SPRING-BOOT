package com.navigators.demo.global.dao.building.impl;

import com.navigators.demo.global.dao.building.BuildingDao;
import com.navigators.demo.global.dto.BuildingDto;
import com.navigators.demo.global.entity.Building;
import com.navigators.demo.global.repository.BuildingRepository;
import com.navigators.demo.global.repository.ContributionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BuildingDaoImpl implements BuildingDao {

    private final ContributionRepository contributionRepository;
    private final BuildingRepository buildingRepository;

    @Autowired
    public BuildingDaoImpl(ContributionRepository contributionRepository,
                           BuildingRepository buildingRepository) {
        this.contributionRepository = contributionRepository;
        this.buildingRepository = buildingRepository;
    }

    @Override
    public Optional<Building> getEntityById(String buildingId) {
        return buildingRepository.findById(buildingId);
    }

    @Override
    public boolean checkOfficialCodeDuplication(String payload) {
        return !buildingRepository.findByOfficialCode(payload).isEmpty();
    }

    @Override
    public boolean checkOfficialCodeDuplicationExceptForSelf(String buildingId, String payload) {
        boolean res = !buildingRepository.findByOfficialCode(payload).isEmpty();
        if (res) {
            return !buildingRepository.findById(buildingId).get().getOfficialCode().equals(payload);
        } else {
            return false;
        }
    }

    @Override
    public List<Building> searchByPayload(String payload) {
        return buildingRepository.findByOfficialCodeContainingOrBuildingNameContainingOrBuildingAliasContaining(payload, payload, payload);
    }

    @Override
    public List<Building> getAll() {
        return buildingRepository.findAll();
    }

    @Override
    public void saveDto(BuildingDto buildingDto) {
        buildingRepository.save(buildingDto.toEntity());
    }

}
