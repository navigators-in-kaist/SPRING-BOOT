package com.navigators.demo.global.entity;

import com.navigators.demo.global.dto.LocationCategoryDto;
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
@Table(name = "location_categories")
public class LocationCategory {

    @Id
    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "description")
    private String description;

    public LocationCategoryDto toDto() {
        return LocationCategoryDto.builder()
                .categoryId(categoryId)
                .categoryName(categoryName)
                .description(description)
                .build();
    }
}
