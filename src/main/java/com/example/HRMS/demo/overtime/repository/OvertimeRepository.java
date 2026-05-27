package com.example.HRMS.demo.overtime.repository;

import com.example.HRMS.demo.overtime.entity.OvertimeEntry;
import com.example.HRMS.demo.overtime.entity.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OvertimeRepository extends JpaRepository<OvertimeEntry, Long> {

    List<OvertimeEntry> findByWorkerIdAndOvertimeDateBetween(
            Long workerId,
            LocalDate start,
            LocalDate end
    );

    List<OvertimeEntry> findByWorkerIdAndOvertimeDateBetweenAndSettlementStatus(
            Long workerId,
            LocalDate start,
            LocalDate end,
            SettlementStatus settlementStatus
    );
}