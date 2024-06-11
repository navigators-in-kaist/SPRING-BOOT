package com.navigators.demo.global.dto;

import com.navigators.demo.global.entity.User;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Setter
public class UserDto {

    private String userUuid;
    private String userId;
    private String userPassword;
    private String userName;
    private boolean isProvenUser;
    private String userEmail;
    private String userStatus;


    public User toEntity() {
        return User.builder()
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
