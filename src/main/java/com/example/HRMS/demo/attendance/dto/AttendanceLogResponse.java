package com.example.HRMS.demo.attendance.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class AttendanceLogResponse {

    private Long attendanceId;

    private Long workerId;

    private String workerName;

    private Long siteId;

    private String siteName;

    private LocalDateTime clockIn;

    private LocalDateTime clockOut;

    private BigDecimal totalHours;

    private BigDecimal overtimeHours;

    private Boolean flagged;
}