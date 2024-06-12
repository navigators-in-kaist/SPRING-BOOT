package com.navigators.demo.global.dto;

import com.navigators.demo.global.entity.LocationCategory;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Setter
public class LocationCategoryDto {

    private String categoryId;
    private String categoryName;
    private String description;

    public LocationCategory toEntity() {
        return LocationCategory.builder()
                .categoryId(categoryId)
                .categoryName(categoryName)
                .description(description)
                .build();
    }

    public Map<String, Object> toMap(Integer locationCount) {
        Map<String, Object> infoMap = new HashMap<>();

        infoMap.put("categoryId", categoryId);
        infoMap.put("categoryName", categoryName);
        infoMap.put("description", description);

        /* additional */
        infoMap.put("numberOfLocations", locationCount);

        return infoMap;
    }
}
