package com.navigators.demo.global.entity;

import com.navigators.demo.global.dto.UserDto;

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
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_uuid")
    private String userUuid;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_password")
    private String userPassword;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "is_proven_user")
    private boolean isProvenUser;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "user_status")
    private String userStatus;


    public UserDto toDto() {
        return UserDto.builder()
                .userUuid(userUuid)
                .userId(userId)
                .userPassword(userPassword)
                .userName(userName)
                .isProvenUser(isProvenUser)
                .userEmail(userEmail)
                .userStatus(userStatus)
                .build();
    }

}
