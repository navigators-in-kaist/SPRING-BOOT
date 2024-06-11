package com.navigators.demo.global.dao.proveTrial;

import com.navigators.demo.global.dto.ProveTrialDto;
import com.navigators.demo.global.entity.ProveTrial;

import java.util.List;
import java.util.Optional;

public interface ProveTrialDao {

    Optional<ProveTrial> getEntityById(String trialId);

    List<ProveTrial> getProveTrialLisOfUser(String userId);

    void saveDto(ProveTrialDto proveTrialDto);
    void deleteDto(ProveTrialDto proveTrialDto);

}
