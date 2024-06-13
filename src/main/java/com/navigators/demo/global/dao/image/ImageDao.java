package com.navigators.demo.global.dao.image;

import com.navigators.demo.global.dto.ImageDto;
import com.navigators.demo.global.entity.Image;

import java.util.List;

public interface ImageDao {

    List<Image> getImageList(String type, String targetEntityId);

    void deleteDto(ImageDto imageDto);
    void deleteById(String imageId);
    void saveDto(ImageDto imageDto);
}
