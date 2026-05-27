package com.example.HRMS.demo.attendance.controller;

import com.example.HRMS.demo.attendance.dto.AttendanceResponse;
import com.example.HRMS.demo.attendance.dto.ClockInRequest;
import com.example.HRMS.demo.attendance.service.AttendanceService;
import com.example.HRMS.demo.cache.ActiveWorkerCache;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/active")
    public List<ActiveWorkerCache> getActiveWorkers() {

        return attendanceService.getActiveWorkers();
    }
}