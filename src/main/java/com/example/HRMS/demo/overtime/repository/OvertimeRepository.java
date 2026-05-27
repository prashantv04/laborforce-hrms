package com.example.HRMS.demo.overtime.repository;

import com.example.HRMS.demo.overtime.entity.OvertimeEntry;
import com.example.HRMS.demo.overtime.entity.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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

    @Query("""
        SELECT COALESCE(SUM(o.overtimeHours), 0)
        FROM OvertimeEntry o
        WHERE o.worker.id = :workerId
        AND o.overtimeDate BETWEEN :startDate AND :endDate
    """)
    BigDecimal getMonthlyOvertimeHours(
            @Param("workerId") Long workerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}