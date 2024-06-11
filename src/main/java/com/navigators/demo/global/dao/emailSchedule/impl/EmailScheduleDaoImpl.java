package com.navigators.demo.global.dao.emailSchedule.impl;

import com.navigators.demo.global.dao.emailSchedule.EmailScheduleDao;
import com.navigators.demo.global.dto.EmailScheduleDto;
import com.navigators.demo.global.entity.EmailSchedule;
import com.navigators.demo.global.repository.EmailScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailScheduleDaoImpl implements EmailScheduleDao {

    private final EmailScheduleRepository emailScheduleRepository;

    @Autowired
    public EmailScheduleDaoImpl(EmailScheduleRepository emailScheduleRepository) {
        this.emailScheduleRepository = emailScheduleRepository;
    }

    @Override
    public void saveDto(EmailScheduleDto emailScheduleDto) {
        emailScheduleRepository.save(emailScheduleDto.toEntity());
    }

    @Override
    public void deleteDto(EmailScheduleDto emailScheduleDto) {
        emailScheduleRepository.delete(emailScheduleDto.toEntity());
    }

    @Override
    public List<EmailSchedule> getEmailScheduleListByTrialId(String trialId) {
        return emailScheduleRepository.findByEmailScheduleProveTrialId(trialId);
    }

}
