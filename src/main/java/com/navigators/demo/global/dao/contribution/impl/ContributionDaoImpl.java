package com.navigators.demo.global.dao.contribution.impl;

import com.navigators.demo.global.dao.contribution.ContributionDao;
import com.navigators.demo.global.dto.ContributionDto;
import com.navigators.demo.global.entity.Contribution;
import com.navigators.demo.global.repository.ContributionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ContributionDaoImpl implements ContributionDao {

    private final ContributionRepository contributionRepository;

    @Autowired
    public ContributionDaoImpl(ContributionRepository contributionRepository) {
        this.contributionRepository = contributionRepository;
    }

    @Override
    public Optional<Contribution> getEntityById(String contributionId) {
        return contributionRepository.findById(contributionId);
    }

    @Override
    public List<Contribution> getAllContributionList() {
        return contributionRepository.findAll();
    }

    @Override
    public List<Contribution> getContributionListByUserUuid(String uuid) {
        return contributionRepository.findByContributionUserId(uuid);
    }

    @Override
    public void deleteDto(ContributionDto contributionDto) {
        contributionRepository.delete(contributionDto.toEntity());
    }

    @Override
    public void saveDto(ContributionDto contributionDto) {
        contributionRepository.save(contributionDto.toEntity());
    }

    @Override
    public List<Contribution> getContributionListByBuildingId(String buildingId) {
        return contributionRepository.findByContributionBuildingId(buildingId);
    }
}
