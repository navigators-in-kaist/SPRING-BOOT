package com.navigators.demo.global.dto;

import com.navigators.demo.global.entity.LocationCategory;
import lombok.*;

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
}
