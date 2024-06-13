package com.navigators.demo.local.image.service.impl;

import com.navigators.demo.codes.ErrorCode;
import com.navigators.demo.global.dao.image.ImageDao;
import com.navigators.demo.global.dto.ImageDto;
import com.navigators.demo.local.image.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageDao imageDao;

    @Autowired
    public ImageServiceImpl(ImageDao imageDao) {
        this.imageDao = imageDao;
    }


    @Override
    public Map<String, Object> addImage(String type, String entityId, String url) {
        Map<String, Object> resultMap = new HashMap<>();

        ImageDto imageDto = ImageDto.builder()
                .id(UUID.randomUUID().toString())
                .imgUrl(url)
                .build();

        if (type.equals("building")) {
            imageDto.setImageBuildingId(entityId);
        } else { /* location */
            imageDto.setImageLocationId(entityId);
        }

        /* save */
        imageDao.saveDto(imageDto);

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }

    @Override
    public Map<String, Object> deleteImage(String imageId) {
        Map<String, Object> resultMap = new HashMap<>();

        imageDao.deleteById(imageId);

        /* no error */
        Map<String, Object> item = new HashMap<>();
        item.put("success", true);
        resultMap.put("item", item);
        resultMap.put("errorCode", ErrorCode.NO_ERROR);
        resultMap.put("httpStatusCode", HttpStatus.OK);
        return resultMap;
    }
}
