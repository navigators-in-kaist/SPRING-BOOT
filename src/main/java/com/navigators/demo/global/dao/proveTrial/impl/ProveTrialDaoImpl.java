package com.navigators.demo.global.dao.proveTrial.impl;

import com.navigators.demo.global.dao.proveTrial.ProveTrialDao;
import com.navigators.demo.global.dto.ProveTrialDto;
import com.navigators.demo.global.entity.ProveTrial;
import com.navigators.demo.global.repository.ProveTrialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProveTrialDaoImpl implements ProveTrialDao {

    private final ProveTrialRepository proveTrialRepository;

    @Autowired
    public ProveTrialDaoImpl(ProveTrialRepository proveTrialRepository) {
        this.proveTrialRepository = proveTrialRepository;
    }

    @Override
    public Optional<ProveTrial> getEntityById(String trialId) {
        return proveTrialRepository.findById(trialId);
    }

    @Override
    public List<ProveTrial> getProveTrialLisOfUser(String userId) {
        return proveTrialRepository.findByProveTrialUserId(userId);
    }

    @Override
    public void saveDto(ProveTrialDto proveTrialDto) {
        proveTrialRepository.save(proveTrialDto.toEntity());
    }

    @Override
    public void deleteDto(ProveTrialDto proveTrialDto) {
        proveTrialRepository.delete(proveTrialDto.toEntity());
    }
}
