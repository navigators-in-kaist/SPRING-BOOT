package com.navigators.demo.scheduler;

import com.navigators.demo.global.entity.EmailSchedule;
import com.navigators.demo.global.repository.EmailScheduleRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class EmailScheduler {

    /* max wait time : 30sec */
    private final Long maxWaitTime = Long.valueOf(30);
    private final Integer MAX_RETRY = 3;
    private final String TITLE_OF_MAIL = "Trailblazer : User proving verification code";

    private final EmailScheduleRepository emailScheduleRepository;
    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailScheduler(EmailScheduleRepository emailScheduleRepository,
                          JavaMailSender javaMailSender) {
        this.emailScheduleRepository = emailScheduleRepository;
        this.javaMailSender = javaMailSender;
    }

    /* per every 5 seconds */
    @Scheduled(fixedDelay = 5000)
    public void schedulerMainLoop() {
        try {
            /* Take all email schedules. */
            List<EmailSchedule> emailSchedules = emailScheduleRepository.findAll();
            /* If not empty, send them. */
            if (!emailSchedules.isEmpty()) {
                for (EmailSchedule tempSchedule : emailSchedules) {
                    boolean sendResultFlag = sendMail(TITLE_OF_MAIL, tempSchedule.getContent(), tempSchedule.getEmailAddr());
                    if (sendResultFlag) {
                        emailScheduleRepository.delete(tempSchedule);
                    } else {
                        tempSchedule.setSendTrialCount(tempSchedule.getSendTrialCount() + 1);
                        if (tempSchedule.getSendTrialCount() >= MAX_RETRY) {
                            emailScheduleRepository.delete(tempSchedule);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean sendMail(String title, String message, String emailAddress) {
        try {
            ArrayList<String> mailReceiverList = new ArrayList<>();
            mailReceiverList.add(emailAddress);
            int numberOfReceivers = mailReceiverList.size();

            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(mailReceiverList.toArray(new String[numberOfReceivers]));

            simpleMailMessage.setSubject(title);
            simpleMailMessage.setText(message);

            javaMailSender.send(simpleMailMessage);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
