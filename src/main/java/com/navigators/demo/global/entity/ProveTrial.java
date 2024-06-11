package com.navigators.demo.global.entity;

import com.navigators.demo.global.dto.ProveTrialDto;
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
@Table(name = "prove_trials")
public class ProveTrial {

    @Id
    @Column(name = "trial_id")
    private String trialId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "verify_code")
    private String verifyCode;

    @Column(name = "proved_at")
    private LocalDateTime provedAt;

    @Column(name = "fk__prove_trials__users")
    private String proveTrialUserId;

    public ProveTrialDto toDto() {
        return ProveTrialDto.builder()
                .trialId(trialId)
                .createdAt(createdAt)
                .verifyCode(verifyCode)
                .provedAt(provedAt)
                .proveTrialUserId(proveTrialUserId)
                .build();
    }

}
