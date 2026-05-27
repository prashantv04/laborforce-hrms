package com.example.HRMS.demo.attendance.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AttendanceResponse {

    private Long attendanceId;

    private Long workerId;

    private String workerName;

    private Long siteId;

    private String siteName;

    private LocalDateTime clockIn;

    private String message;
}