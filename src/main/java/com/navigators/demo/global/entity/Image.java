package com.navigators.demo.global.entity;

import com.navigators.demo.global.dto.ImageDto;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "images")
public class Image {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "img_url", columnDefinition = "TEXT")
    private String imgUrl;

    @Column(name = "fk__images__buildings")
    private String imageBuildingId;

    @Column(name = "fk__images__locations")
    private String imageLocationId;

    public ImageDto toDto() {
        return ImageDto.builder()
                .id(id)
                .imgUrl(imgUrl)
                .imageBuildingId(imageBuildingId)
                .imageLocationId(imageLocationId)
                .build();
    }

}
