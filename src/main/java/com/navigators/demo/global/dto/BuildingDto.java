package com.navigators.demo.global.dto;

import com.navigators.demo.global.entity.Building;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public Map<String, Object> toMap() {
        Map<String, Object> infoMap = new HashMap<>();

        infoMap.put("buildingId", buildingId);
        infoMap.put("officialCode", officialCode);
        infoMap.put("buildingName", buildingName);
        infoMap.put("buildingAlias", buildingAlias);
        infoMap.put("description", description);
        infoMap.put("maxFloor", maxFloor);
        infoMap.put("longitude", longitude);
        infoMap.put("latitude", latitude);

        return infoMap;
    }
}
