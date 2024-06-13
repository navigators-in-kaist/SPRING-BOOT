package com.navigators.demo.local.contribution.service.impl;

import com.navigators.demo.codes.ContributionStatusCode;
import com.navigators.demo.codes.ContributionType;
import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.global.dao.building.BuildingDao;
import com.navigators.demo.global.dao.contribution.ContributionDao;
import com.navigators.demo.global.dao.location.LocationDao;
import com.navigators.demo.global.dao.user.UserDao;
import com.navigators.demo.global.dto.BuildingDto;
import com.navigators.demo.global.dto.ContributionDto;
import com.navigators.demo.global.dto.LocationDto;
import com.navigators.demo.global.entity.Building;
import com.navigators.demo.global.entity.Contribution;
import com.navigators.demo.global.entity.User;
import com.navigators.demo.local.contribution.service.ContributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ContributionServiceImpl implements ContributionService {

    private final ContributionDao contributionDao;
    private final UserDao userDao;
    private final BuildingDao buildingDao;
    private final LocationDao locationDao;

    @Autowired
    public ContributionServiceImpl(ContributionDao contributionDao,
                                   UserDao userDao,
                                   BuildingDao buildingDao,
                                   LocationDao locationDao) {
        this.contributionDao = contributionDao;
        this.userDao = userDao;
        this.buildingDao = buildingDao;
        this.locationDao = locationDao;
    }

    @Override
    public Map<String, Object> getContributionListByUserId(String userId) {
        Map<String, Object> resultMap = new HashMap<>();

        /* get uuid of the user */
        String userUuid = userDao.getUserEntityById(userId).get().getUserUuid();

        List<Contribution> contributionList = contributionDao.getContributionListByUserUuid(userUuid);

        Map<String, Object> item = new HashMap<>();
        item.put("contributionList", contributionList);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;

    }

    @Override
    public Map<String, Object> getAllContributionList() {
        Map<String, Object> resultMap = new HashMap<>();

        List<Contribution> contributionList = contributionDao.getAllContributionList();

        Map<String, Object> item = new HashMap<>();
        item.put("contributionList", contributionList);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

    @Override
    public Map<String, Object> deleteContribution(String userId, String contributionId) {
        Map<String, Object> resultMap = new HashMap<>();

        /* check user and contribution combination */
        User targetUser = userDao.getUserEntityById(userId).get();
        Contribution targetContribution = contributionDao.getEntityById(contributionId).get();
        if (!targetUser.getUserUuid().equals(targetContribution.getContributionUserId())) {
            resultMap.put("errorCode", ErrorCode.DELETE_INVALID_PARAM);
            resultMap.put("reason", "The given contribution is not of the given user.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /* status check */
        if (!targetContribution.getContributionStatus().equals(ContributionStatusCode.WAIT_FOR_ACCEPT)) {
            resultMap.put("errorCode", ErrorCode.DELETE_INVALID_PARAM);
            resultMap.put("reason", "The contribution is already accepted or rejected.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /* delete */
        contributionDao.deleteDto(targetContribution.toDto());

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

    @Override
    public Map<String, Object> addBuildingContribution(String userId, Map<String, Object> requestBody) {
        Map<String, Object> resultMap = new HashMap<>();

        User targetUser = userDao.getUserEntityById(userId).get();

        /* official code duplication check */
        if (buildingDao.checkOfficialCodeDuplication((String) requestBody.get("officialCode"))) {
            resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
            resultMap.put("reason", "The official code of the building is already existing.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /* craft a new contribution dto */
        ContributionDto contributionDto = ContributionDto.builder()
                .contributionId(UUID.randomUUID().toString())
                .contributionType(ContributionType.BUILDING)
                .contributionStatus(ContributionStatusCode.WAIT_FOR_ACCEPT)
                .createdAt(LocalDateTime.now())
                .approvedAt(null)
                .officialCode((String) requestBody.get("officialCode"))
                .name((String) requestBody.get("buildingName"))
                .alias((String) requestBody.get("buildingAlias"))
                .maxFloor(Integer.parseInt(requestBody.get("maxFloor").toString()))
                .longitude(Double.parseDouble(requestBody.get("longitude").toString()))
                .latitude(Double.parseDouble(requestBody.get("latitude").toString()))
                .floor(null)
                .description((String) requestBody.get("description"))
                .roomNumber(null)
                .contributionUserId(targetUser.getUserUuid())
                .contributionBuildingId(null)
                .contributionLocationCategoryId(null)
                .build();

        /* save */
        contributionDao.saveDto(contributionDto);

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

    @Override
    public Map<String, Object> addLocationContribution(String userId, Map<String, Object> requestBody) {
        Map<String, Object> resultMap = new HashMap<>();

        User targetUser = userDao.getUserEntityById(userId).get();

        /* check floor validity */
        Building targetBuilding = buildingDao.getEntityById((String) requestBody.get("locationBuildingId")).get();
        if (Integer.parseInt(requestBody.get("locationFloor").toString()) > targetBuilding.getMaxFloor()) {
            resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
            resultMap.put("reason", "The given building has " + targetBuilding.getMaxFloor() + " floors at most.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /* craft a new contribution dto */
        ContributionDto contributionDto = ContributionDto.builder()
                .contributionId(UUID.randomUUID().toString())
                .contributionType(ContributionType.LOCATION)
                .contributionStatus(ContributionStatusCode.WAIT_FOR_ACCEPT)
                .createdAt(LocalDateTime.now())
                .approvedAt(null)
                .officialCode(null)
                .name((String) requestBody.get("locationName"))
                .alias(null)
                .maxFloor(null)
                .longitude(null)
                .latitude(null)
                .floor(Integer.parseInt(requestBody.get("locationFloor").toString()))
                .description((String) requestBody.get("description"))
                .contributionUserId(targetUser.getUserUuid())
                .contributionBuildingId((String) requestBody.get("locationBuildingId"))
                .contributionLocationCategoryId((String) requestBody.get("locationCategoryId"))
                .build();

        if (requestBody.get("roomNumber") != null) {
            contributionDto.setRoomNumber((String) requestBody.get("roomNumber"));
        }

        /* save */
        contributionDao.saveDto(contributionDto);

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

    @Override
    public Map<String, Object> checkOfficialCodeDuplication(String payload) {
        Map<String, Object> resultMap = new HashMap<>();

        boolean res = buildingDao.checkOfficialCodeDuplication(payload);

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("isDuplicated", res);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

    @Override
    public Map<String, Object> approveContribution(String contributionId) {
        Map<String, Object> resultMap = new HashMap<>();

        /* get target contribution */
        Contribution targetContribution = contributionDao.getEntityById(contributionId).get();

        /* check status */
        if (!targetContribution.getContributionStatus().equals(ContributionStatusCode.WAIT_FOR_ACCEPT)) {
            resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
            resultMap.put("reason", "The contribution is already approved or rejected.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /* craft a new dto (building or location) */
        if (targetContribution.getContributionType().equals(ContributionType.BUILDING)) {
            BuildingDto buildingDto = BuildingDto.builder()
                    .buildingId(UUID.randomUUID().toString())
                    .officialCode(targetContribution.getOfficialCode())
                    .buildingName(targetContribution.getName())
                    .buildingAlias(targetContribution.getAlias())
                    .description(targetContribution.getDescription())
                    .maxFloor(targetContribution.getMaxFloor())
                    .longitude(targetContribution.getLongitude())
                    .latitude(targetContribution.getLatitude())
                    .build();
            buildingDao.saveDto(buildingDto);
        } else {
            LocationDto locationDto = LocationDto.builder()
                    .locationId(UUID.randomUUID().toString())
                    .locationName(targetContribution.getName())
                    .locationFloor(targetContribution.getFloor())
                    .description(targetContribution.getDescription())
                    .roomNumber(targetContribution.getRoomNumber())
                    .locationBuildingId(targetContribution.getContributionBuildingId())
                    .locationCategoryId(targetContribution.getContributionLocationCategoryId())
                    .build();
            locationDao.saveDto(locationDto);
        }

        /* contribution dto value set up */
        targetContribution.setContributionStatus(ContributionStatusCode.ACCEPTED);
        targetContribution.setApprovedAt(LocalDateTime.now());

        /* save */
        contributionDao.saveDto(targetContribution.toDto());

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

    @Override
    public Map<String, Object> rejectContribution(String contributionId) {
        Map<String, Object> resultMap = new HashMap<>();

        /* get target contribution */
        Contribution targetContribution = contributionDao.getEntityById(contributionId).get();

        /* check status */
        if (!targetContribution.getContributionStatus().equals(ContributionStatusCode.WAIT_FOR_ACCEPT)) {
            resultMap.put("errorCode", ErrorCode.POST_INVALID_PARAM);
            resultMap.put("reason", "The contribution is already approved or rejected.");
            resultMap.put("httpStatusCode", HttpStatus.BAD_REQUEST);
            return resultMap;
        }

        /* contribution dto value set up */
        targetContribution.setContributionStatus(ContributionStatusCode.REJECTED);
        targetContribution.setApprovedAt(null);

        /* save */
        contributionDao.saveDto(targetContribution.toDto());

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

}
