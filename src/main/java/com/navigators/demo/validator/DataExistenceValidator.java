package com.navigators.demo.validator;

import com.navigators.demo.codes.UserStatusCode;
import com.navigators.demo.global.entity.Admin;
import com.navigators.demo.global.entity.User;
import com.navigators.demo.global.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataExistenceValidator {

    private final UserRepository userRepository;
    private final ProveTrialRepository proveTrialRepository;
    private final AdminRepository adminRepository;
    private final ContributionRepository contributionRepository;
    private final LocationCategoryRepository locationCategoryRepository;
    private final LocationRepository locationRepository;
    private final BuildingRepository buildingRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final ImageRepository imageRepository;


    @Autowired
    public DataExistenceValidator(UserRepository userRepository,
                                  ProveTrialRepository proveTrialRepository,
                                  AdminRepository adminRepository,
                                  ContributionRepository contributionRepository,
                                  LocationCategoryRepository locationCategoryRepository,
                                  LocationRepository locationRepository,
                                  BuildingRepository buildingRepository,
                                  SearchHistoryRepository searchHistoryRepository,
                                  ImageRepository imageRepository) {
        this.userRepository = userRepository;
        this.proveTrialRepository = proveTrialRepository;
        this.adminRepository = adminRepository;
        this.contributionRepository = contributionRepository;
        this.locationCategoryRepository = locationCategoryRepository;
        this.locationRepository = locationRepository;
        this.buildingRepository = buildingRepository;
        this.searchHistoryRepository = searchHistoryRepository;
        this.imageRepository = imageRepository;
    }


    public boolean isUserExist(String userId) {
        return userRepository.findByUserId(userId).isPresent();
    }

    public boolean isAdminExist(String adminId) {
        return adminRepository.findByAdminId(adminId).isPresent();
    }

    public boolean isProveTrialExist(String trialId) {
        return proveTrialRepository.findById(trialId).isPresent();
    }

    public boolean isContributionExist(String contributionId) {
        return contributionRepository.findById(contributionId).isPresent();
    }

    public boolean isLocationCategoryExist(String locationCategoryId) {
        return locationCategoryRepository.findById(locationCategoryId).isPresent();
    }

    public boolean isBuildingExist(String buildingId) {
        return buildingRepository.findById(buildingId).isPresent();
    }

    public boolean isLocationExist(String locationId) {
        return locationRepository.findById(locationId).isPresent();
    }

    public boolean isSearchHistoryExist(String historyId) {
        return searchHistoryRepository.findById(historyId).isPresent();
    }

    public boolean isImageExist(String imageId) {
        return imageRepository.findById(imageId).isPresent();
    }


    public boolean isUserACTIVE(String userId) {
        User targetUser = userRepository.findByUserId(userId).get();

        return targetUser.getUserStatus().equals(UserStatusCode.ACTIVE);
    }

    public boolean isAdminACTIVE(String adminId) {
        Admin targetAdmin = adminRepository.findByAdminId(adminId).get();

        return !targetAdmin.getAdminName().startsWith("_Deleted");
    }
}
