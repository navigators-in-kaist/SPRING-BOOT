package com.navigators.demo.local.image.service;

import java.util.Map;

public interface ImageService {

    Map<String, Object> addImage(String type, String entityId, String url);
    Map<String, Object> deleteImage(String imageId);

}
