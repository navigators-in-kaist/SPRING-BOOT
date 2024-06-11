package com.navigators.demo.global.dto;

import com.navigators.demo.global.entity.Contribution;
import lombok.*;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Setter
public class ContributionDto {

    private String contributionId;
    private String contributionType;
    private String contributionStatus;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private String officialCode;
    private String name;
    private String alias;
    private Integer maxFloor;
    private Double longitude;
    private Double latitude;
    private Integer floor;
    private String description;
    private String roomNumber;
    private String contributionUserId;
    private String contributionBuildingId;
    private String contributionLocationCategoryId;

    public Contribution toEntity() {
        return Contribution.builder()
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
