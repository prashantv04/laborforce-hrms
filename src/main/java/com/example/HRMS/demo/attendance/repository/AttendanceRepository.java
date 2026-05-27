package com.example.HRMS.demo.attendance.repository;

import com.example.HRMS.demo.attendance.entity.AttendanceLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceLog, Long> {

    Optional<AttendanceLog> findByWorkerIdAndClockOutIsNull(Long workerId);

    @EntityGraph(attributePaths = {"worker", "site"})
    Page<AttendanceLog> findByWorkerIdAndClockInBetween(
            Long workerId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );
}