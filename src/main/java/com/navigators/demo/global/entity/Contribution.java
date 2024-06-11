package com.navigators.demo.global.entity;

import com.navigators.demo.global.dto.ContributionDto;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "contributions")
public class Contribution {

    @Id
    @Column(name = "contribution_id")
    private String contributionId;

    @Column(name = "contribution_type")
    private String contributionType;

    @Column(name = "contribution_status")
    private String contributionStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "official_code")
    private String officialCode;

    @Column(name = "name")
    private String name;

    @Column(name = "alias")
    private String alias;

    @Column(name = "max_floor")
    private Integer maxFloor;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "floor")
    private Integer floor;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "fk__contributions__users")
    private String contributionUserId;

    @Column(name = "fk__contributions__buildings")
    private String contributionBuildingId;

    @Column(name = "fk__contributions__location_categories")
    private String contributionLocationCategoryId;

    public ContributionDto toDto() {
        return ContributionDto.builder()
                .contributionId(contributionId)
                .contributionType(contributionType)
                .contributionStatus(contributionStatus)
                .createdAt(createdAt)
                .approvedAt(approvedAt)
                .officialCode(officialCode)
                .name(name)
                .alias(alias)
                .maxFloor(maxFloor)
                .longitude(longitude)
                .latitude(latitude)
                .floor(floor)
                .description(description)
                .roomNumber(roomNumber)
                .contributionUserId(contributionUserId)
                .contributionBuildingId(contributionBuildingId)
                .contributionLocationCategoryId(contributionLocationCategoryId)
                .build();
    }
}
