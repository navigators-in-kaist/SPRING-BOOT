package com.navigators.demo.global.dao.image.impl;

import com.navigators.demo.global.dao.image.ImageDao;
import com.navigators.demo.global.entity.Image;
import com.navigators.demo.global.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ImageDaoImpl implements ImageDao {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageDaoImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public List<Image> getImageList(String type, String targetEntityId) {
        if (type.equals("building")) {
            return imageRepository.findByImageBuildingId(targetEntityId);
        } else {
            return imageRepository.findByImageLocationId(targetEntityId);
        }
    }

}
