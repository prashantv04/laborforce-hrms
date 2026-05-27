package com.example.HRMS.demo.attendance.repository;

import com.example.HRMS.demo.attendance.entity.AttendanceLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AttendanceRepository
        extends JpaRepository<AttendanceLog, Long> {

    Optional<AttendanceLog> findByWorkerIdAndClockOutIsNull(
            Long workerId
    );

    @Query("""
        SELECT a
        FROM AttendanceLog a
        JOIN FETCH a.worker
        JOIN FETCH a.site
        WHERE a.worker.id = :workerId
        AND a.clockIn BETWEEN :from AND :to
        """)
    Page<AttendanceLog> findAttendanceLogs(
            Long workerId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );
}