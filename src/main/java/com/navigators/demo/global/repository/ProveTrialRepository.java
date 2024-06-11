package com.navigators.demo.global.repository;

import com.navigators.demo.global.entity.ProveTrial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProveTrialRepository extends JpaRepository<ProveTrial, String> {

    Optional<ProveTrial> findById(String trialId);

    List<ProveTrial> findAll();
    List<ProveTrial>  findByProveTrialUserId(String userId);

}
