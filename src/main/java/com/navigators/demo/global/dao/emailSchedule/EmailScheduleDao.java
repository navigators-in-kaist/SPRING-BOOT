package com.navigators.demo.global.dao.emailSchedule;

import com.navigators.demo.global.dto.EmailScheduleDto;
import com.navigators.demo.global.entity.EmailSchedule;

import java.util.List;

public interface EmailScheduleDao {

    void saveDto(EmailScheduleDto emailScheduleDto);
    void deleteDto(EmailScheduleDto emailScheduleDto);

    List<EmailSchedule> getEmailScheduleListByTrialId(String trialId);
}
