package com.navigators.demo.global.dto;

import com.navigators.demo.global.entity.EmailSchedule;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Setter
public class EmailScheduleDto {

    private String scheduleId;
    private String content;
    private Integer sendTrialCount;
    private String emailAddr;
    private boolean isSuccess;
    private String emailScheduleProveTrialId;

    public EmailSchedule toEntity() {
        return EmailSchedule.builder()
                .scheduleId(scheduleId)
                .content(content)
                .sendTrialCount(sendTrialCount)
                .emailAddr(emailAddr)
                .isSuccess(isSuccess)
                .emailScheduleProveTrialId(emailScheduleProveTrialId)
                .build();
    }
}
