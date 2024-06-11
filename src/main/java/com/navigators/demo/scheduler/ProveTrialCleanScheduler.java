package com.navigators.demo.scheduler;

import com.navigators.demo.global.entity.EmailSchedule;
import com.navigators.demo.global.entity.ProveTrial;
import com.navigators.demo.global.repository.EmailScheduleRepository;
import com.navigators.demo.global.repository.ProveTrialRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Log4j2
public class ProveTrialCleanScheduler {

    private final static Integer TTL = 360; /* in sec, 6 min */

    private final ProveTrialRepository proveTrialRepository;
    private final EmailScheduleRepository emailScheduleRepository;

    @Autowired
    public ProveTrialCleanScheduler(ProveTrialRepository proveTrialRepository, EmailScheduleRepository emailScheduleRepository) {
        this.proveTrialRepository = proveTrialRepository;
        this.emailScheduleRepository = emailScheduleRepository;
    }

    /* every a minute */
    @Scheduled(fixedDelay = 60000) /* im ms */
    public void cleanUpProveTrials() {
        log.info("Prove trial clean up scheduler start ...");
        Integer deletedCount = 0;
        LocalDateTime now = LocalDateTime.now();
        List<ProveTrial> proveTrialList = proveTrialRepository.findAll();
        for (ProveTrial proveTrial : proveTrialList) {
            if (proveTrial.getCreatedAt().plusSeconds(TTL).isBefore(now)) {
                deletedCount++;
                /* get email schedules */
                List<EmailSchedule> emailScheduleList = emailScheduleRepository.findByEmailScheduleProveTrialId(proveTrial.getTrialId());
                if (!emailScheduleList.isEmpty()) {
                    for (EmailSchedule emailSchedule : emailScheduleList) {
                        emailScheduleRepository.delete(emailSchedule);
                    }
                }
                proveTrialRepository.delete(proveTrial);
            }
        }
        log.info(deletedCount + " trials were cleaned up.");
    }

}
