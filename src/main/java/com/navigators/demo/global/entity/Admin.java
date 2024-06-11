package com.navigators.demo.global.entity;

import com.navigators.demo.global.dto.AdminDto;
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
@Table(name = "admins")
public class Admin {

    @Id
    @Column(name = "admin_uuid")
    private String adminUuid;

    @Column(name = "admin_id")
    private String adminId;

    @Column(name = "admin_name")
    private String adminName;

    @Column(name = "admin_password")
    private String adminPassword;

    public AdminDto toDto() {
        return AdminDto.builder()
                .adminUuid(adminUuid)
                .adminId(adminId)
                .adminName(adminName)
                .adminPassword(adminPassword)
                .build();
    }
}
