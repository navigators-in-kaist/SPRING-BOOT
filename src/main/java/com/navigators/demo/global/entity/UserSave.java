package com.navigators.demo.global.entity;

import com.navigators.demo.global.dto.UserSaveDto;
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
@Table(name = "user_saves")
public class UserSave {

    @Id
    @Column(name = "mapping_id")
    private String mappingId;

    @Column(name = "fk__user_saves__users")
    private String userSaveUserId;

    @Column(name = "fk__user_saves__buildings")
    private String userSaveBuildingId;

    @Column(name = "fk__user_saves__locations")
    private String userSaveLocationId;

    public UserSaveDto toDto() {
        return UserSaveDto.builder()
                .mappingId(mappingId)
                .userSaveUserId(userSaveUserId)
                .userSaveBuildingId(userSaveBuildingId)
                .userSaveLocationId(userSaveLocationId)
                .build();
    }

}
