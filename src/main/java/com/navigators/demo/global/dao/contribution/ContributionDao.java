package com.navigators.demo.global.dao.contribution;

import com.navigators.demo.global.dto.ContributionDto;
import com.navigators.demo.global.entity.Contribution;

import java.util.List;
import java.util.Optional;

public interface ContributionDao {

    Optional<Contribution> getEntityById(String contributionId);

    List<Contribution> getAllContributionList();
    List<Contribution> getContributionListByUserUuid(String uuid);

    void deleteDto(ContributionDto contributionDto);
    void saveDto(ContributionDto contributionDto);

}
