package com.navigators.demo.global.entity;

import com.navigators.demo.global.dto.BuildingDto;
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
@Table(name = "buildings")
public class Building {

    @Id
    @Column(name = "building_id")
    private String buildingId;

    @Column(name = "official_code")
    private String officialCode;

    @Column(name = "building_name")
    private String buildingName;

    @Column(name = "building_alias")
    private String buildingAlias;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_floor")
    private Integer maxFloor;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    public BuildingDto toDto() {
        return BuildingDto.builder()
                .buildingId(buildingId)
                .officialCode(officialCode)
                .buildingName(buildingName)
                .buildingAlias(buildingAlias)
                .description(description)
                .maxFloor(maxFloor)
                .longitude(longitude)
                .latitude(latitude)
                .build();
    }
}
