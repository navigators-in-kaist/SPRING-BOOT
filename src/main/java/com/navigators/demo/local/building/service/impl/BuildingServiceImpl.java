package com.navigators.demo.local.building.service.impl;

import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.global.dao.building.BuildingDao;
import com.navigators.demo.global.dao.image.ImageDao;
import com.navigators.demo.global.dao.searchHistory.SearchHistoryDao;
import com.navigators.demo.global.dao.user.UserDao;
import com.navigators.demo.global.dao.userSave.UserSaveDao;
import com.navigators.demo.global.entity.Image;
import com.navigators.demo.global.entity.User;
import com.navigators.demo.local.building.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BuildingServiceImpl implements BuildingService {

    private final UserDao userDao;
    private final BuildingDao buildingDao;
    private final ImageDao imageDao;
    private final UserSaveDao userSaveDao;
    private final SearchHistoryDao searchHistoryDao;

    @Autowired
    public BuildingServiceImpl(UserDao userDao,
                               BuildingDao buildingDao,
                               ImageDao imageDao,
                               UserSaveDao userSaveDao,
                               SearchHistoryDao searchHistoryDao) {
        this.userDao = userDao;
        this.buildingDao = buildingDao;
        this.imageDao = imageDao;
        this.userSaveDao = userSaveDao;
        this.searchHistoryDao = searchHistoryDao;
    }


    /* If user request, "isSaved" is included and search history is added as a side effect. */
    @Override
    public Map<String, Object> getBuildingDetailedInfo(String coverage, @Nullable String userId, String buildingId) {
        Map<String, Object> resultMap = new HashMap<>();

        /* get building information */
        Map<String, Object> infoMap = buildingDao.getEntityById(buildingId).get().toDto().toMap();

        /* get image list */
        List<Image> imageList = imageDao.getImageList("building", buildingId);
        infoMap.put("imageList", imageList);

        /** (additional) for user requests */
        if (coverage.equals("user") && (userId != null)) {
            /* target user */
            User targetUser = userDao.getUserEntityById(userId).get();

            /* get isSaved for the user */
            String pseudoId = targetUser.getUserUuid() + buildingId;
            infoMap.put("isSaved", userSaveDao.getEntityById(pseudoId).isPresent());

            /* add or update search history */
            searchHistoryDao.addOrUpdatedEntity(targetUser.getUserUuid(), "building", buildingId);
        }

        /* no error */
        resultMap.put("item", infoMap);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }


}
