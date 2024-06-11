package com.navigators.demo.global.entity;

import com.navigators.demo.global.dto.LocationDto;
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
@Table(name = "locations")
public class Location {

    @Id
    @Column(name = "location_id")
    private String locationId;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "location_floor")
    private Integer locationFloor;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "fk__locations__buildings")
    private String locationBuildingId;

    @Column(name = "fk__locations__location_categories")
    private String locationCategoryId;

    public LocationDto toDto() {
        return LocationDto.builder()
                .locationId(locationId)
                .locationName(locationName)
                .locationFloor(locationFloor)
                .description(description)
                .roomNumber(roomNumber)
                .locationBuildingId(locationBuildingId)
                .locationCategoryId(locationCategoryId)
                .build();
    }

}
