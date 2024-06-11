package com.navigators.demo.global.dto;

import com.navigators.demo.global.entity.Location;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Setter
public class LocationDto {

    private String locationId;
    private String locationName;
    private Integer locationFloor;
    private String description;
    private String roomNumber;
    private String locationBuildingId;
    private String locationCategoryId;

    public Location toEntity() {
        return Location.builder()
                .locationId(locationId)
                .locationName(locationName)
                .locationFloor(locationFloor)
                .description(description)
                .roomNumber(roomNumber)
                .locationBuildingId(locationBuildingId)
                .locationCategoryId(locationCategoryId)
                .build();
    }

    public Map<String, Object> toMap(Double distance,
                                     String categoryName,
                                     String buildingName,
                                     String officialCode,
                                     Double longitude,
                                     Double latitude) {

        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("locationId", this.locationId);
        resultMap.put("locationName", this.locationName);
        resultMap.put("locationFloor", this.locationFloor);
        resultMap.put("description", this.description);
        resultMap.put("roomNumber", this.roomNumber);
        resultMap.put("locationBuildingId", this.locationBuildingId);
        resultMap.put("locationCategoryId", this.locationCategoryId);
        /* additional */
        resultMap.put("distanceFromCurrentPosition", distance);
        resultMap.put("locationCategoryName", categoryName);
        resultMap.put("locationBuildingName", buildingName);
        resultMap.put("locationBuildingOfficialCode", officialCode);
        resultMap.put("longitude", longitude);
        resultMap.put("latitude", latitude);

        return resultMap;
    }
}
