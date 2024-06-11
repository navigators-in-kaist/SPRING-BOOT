package com.navigators.demo.global.entity;

import com.navigators.demo.global.dto.EmailScheduleDto;
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
@Table(name = "email_schedules")
public class EmailSchedule {

    @Id
    @Column(name = "schedule_id")
    private String scheduleId;

    @Column(name = "content")
    private String content;

    @Column(name = "send_trial_count")
    private Integer sendTrialCount;

    @Column(name = "email_addr")
    private String emailAddr;

    @Column(name = "is_success")
    private boolean isSuccess;

    @Column(name = "fk__email_schedules__prove_trials")
    private String emailScheduleProveTrialId;

    public EmailScheduleDto toDto() {
        return EmailScheduleDto.builder()
                .scheduleId(scheduleId)
                .content(content)
                .sendTrialCount(sendTrialCount)
                .emailAddr(emailAddr)
                .isSuccess(isSuccess)
                .emailScheduleProveTrialId(emailScheduleProveTrialId)
                .build();
    }

}
