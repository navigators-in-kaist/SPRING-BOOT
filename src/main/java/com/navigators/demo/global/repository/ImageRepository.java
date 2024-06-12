package com.navigators.demo.global.repository;

import com.navigators.demo.global.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, String> {

    List<Image> findByImageBuildingId(String buildingId);
    List<Image> findByImageLocationId(String imageLocationId);

}
