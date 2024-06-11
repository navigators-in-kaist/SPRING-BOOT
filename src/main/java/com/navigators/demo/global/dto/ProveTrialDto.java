package com.navigators.demo.global.dto;

import com.navigators.demo.global.entity.ProveTrial;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Setter
public class ProveTrialDto {

    private String trialId;
    private LocalDateTime createdAt;
    private String verifyCode;
    private LocalDateTime provedAt;
    private String proveTrialUserId;

    public ProveTrial toEntity() {
        return ProveTrial.builder()
                .trialId(trialId)
                .createdAt(createdAt)
                .verifyCode(verifyCode)
                .provedAt(provedAt)
                .proveTrialUserId(proveTrialUserId)
                .build();
    }
}
