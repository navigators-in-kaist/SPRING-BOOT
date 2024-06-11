package com.navigators.demo.global.dto;

import com.navigators.demo.global.entity.Image;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Setter
public class ImageDto {

    private String id;
    private String imgUrl;
    private String imageBuildingId;
    private String imageLocationId;

    public Image toEntity() {
        return Image.builder()
                .id(id)
                .imgUrl(imgUrl)
                .imageBuildingId(imageBuildingId)
                .imageLocationId(imageLocationId)
                .build();
    }
}
