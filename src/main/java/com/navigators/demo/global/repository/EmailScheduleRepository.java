package com.navigators.demo.global.repository;

import com.navigators.demo.global.entity.EmailSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailScheduleRepository extends JpaRepository<EmailSchedule, String> {

    List<EmailSchedule> findAll();
    List<EmailSchedule> findByEmailScheduleProveTrialId(String proveTrialId);

}
