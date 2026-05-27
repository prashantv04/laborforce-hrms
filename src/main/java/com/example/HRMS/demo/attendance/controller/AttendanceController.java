package com.example.HRMS.demo.attendance.controller;

import com.example.HRMS.demo.attendance.dto.AttendanceLogResponse;
import com.example.HRMS.demo.attendance.dto.AttendanceResponse;
import com.example.HRMS.demo.attendance.dto.ClockInRequest;
import com.example.HRMS.demo.attendance.dto.ClockOutRequest;
import com.example.HRMS.demo.attendance.service.AttendanceService;
import com.example.HRMS.demo.cache.ActiveWorkerCache;
import com.example.HRMS.demo.common.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/clock-in")
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceResponse clockIn(
            @Valid @RequestBody ClockInRequest request
    ) {

        return attendanceService.clockIn(request);
    }

    @PostMapping("/clock-out")
    public AttendanceResponse clockOut(
            @Valid @RequestBody ClockOutRequest request
    ) {

        return attendanceService.clockOut(request);
    }

    @GetMapping("/active")
    public List<ActiveWorkerCache> getActiveWorkers() {

        return attendanceService.getActiveWorkers();
    }

    @GetMapping("/log")
    public PagedResponse<AttendanceLogResponse> getAttendanceLogs(

            @RequestParam
            Long workerId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {

        return attendanceService.getAttendanceLogs(
                workerId,
                from,
                to,
                page,
                size
        );
    }
}