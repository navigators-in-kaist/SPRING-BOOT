package com.navigators.demo.global.dto;

import com.navigators.demo.global.entity.Admin;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Setter
public class AdminDto {

    private String adminUuid;
    private String adminId;
    private String adminName;
    private String adminPassword;

    public Admin toEntity() {
        return Admin.builder()
                .adminUuid(adminUuid)
                .adminId(adminId)
                .adminName(adminName)
                .adminPassword(adminPassword)
                .build();
    }
}
