package com.navigators.demo.local.contribution.service;

import java.util.Map;

public interface ContributionService {

    Map<String, Object> getContributionListByUserId(String userId);
    Map<String, Object> getAllContributionList();
    Map<String, Object> deleteContribution(String userId, String contributionId);
    Map<String, Object> addBuildingContribution(String userId, Map<String, Object> requestBody);
    Map<String, Object> addLocationContribution(String userId, Map<String, Object> requestBody);
    Map<String, Object> checkOfficialCodeDuplication(String payload);

}
