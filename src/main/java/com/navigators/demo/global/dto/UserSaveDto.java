package com.navigators.demo.global.dto;

import com.navigators.demo.global.entity.UserSave;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Setter
public class UserSaveDto {

    private String mappingId;
    private String userSaveUserId;
    private String userSaveBuildingId;
    private String userSaveLocationId;

    public UserSave toEntity() {
        return UserSave.builder()
                .mappingId(mappingId)
                .userSaveUserId(userSaveUserId)
                .userSaveBuildingId(userSaveBuildingId)
                .userSaveLocationId(userSaveLocationId)
                .build();
    }
}
