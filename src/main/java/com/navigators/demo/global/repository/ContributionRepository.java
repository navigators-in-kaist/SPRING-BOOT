package com.navigators.demo.global.repository;

import com.navigators.demo.global.entity.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContributionRepository extends JpaRepository<Contribution, String> {

    Optional<Contribution> findById(String contributionId);

    List<Contribution> findAll();
    List<Contribution> findByContributionUserId(String userUuid);
    List<Contribution> findByContributionBuildingId(String buildingId);

}
