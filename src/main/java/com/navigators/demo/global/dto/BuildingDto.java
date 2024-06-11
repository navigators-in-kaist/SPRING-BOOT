package com.navigators.demo.global.dto;

import com.navigators.demo.global.entity.Building;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Setter
public class BuildingDto {

    private String buildingId;
    private String officialCode;
    private String buildingName;
    private String buildingAlias;
    private String description;
    private Integer maxFloor;
    private Double longitude;
    private Double latitude;

    public Building toEntity() {
        return Building.builder()
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
