package com.example.HRMS.demo.attendance.service;

import com.example.HRMS.demo.attendance.dto.AttendanceResponse;
import com.example.HRMS.demo.attendance.dto.ClockInRequest;
import com.example.HRMS.demo.attendance.entity.AttendanceLog;
import com.example.HRMS.demo.attendance.repository.AttendanceRepository;
import com.example.HRMS.demo.cache.ActiveWorkerCache;
import com.example.HRMS.demo.cache.ActiveWorkerCacheService;
import com.example.HRMS.demo.common.exception.ConflictException;
import com.example.HRMS.demo.common.exception.ResourceNotFoundException;
import com.example.HRMS.demo.common.exception.ValidationException;
import com.example.HRMS.demo.site.entity.Site;
import com.example.HRMS.demo.site.repository.SiteRepository;
import com.example.HRMS.demo.worker.entity.Worker;
import com.example.HRMS.demo.worker.repository.WorkerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    private final WorkerRepository workerRepository;

    private final SiteRepository siteRepository;

    private final ActiveWorkerCacheService activeWorkerCacheService;

    @Transactional
    public AttendanceResponse clockIn(ClockInRequest request) {

        Worker worker = workerRepository.findById(request.getWorkerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Worker not found"));

        if (!worker.getActive()) {
            throw new ValidationException("Worker is inactive");
        }

        Site site = siteRepository.findById(request.getSiteId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Site not found"));

        if (!site.getActive()) {
            throw new ValidationException("Site is inactive");
        }

        attendanceRepository.findByWorkerIdAndClockOutIsNull(worker.getId())
                .ifPresent(existing -> {
                    throw new ConflictException(
                            "Worker is already clocked in"
                    );
                });

        AttendanceLog attendance = new AttendanceLog();

        attendance.setWorker(worker);
        attendance.setSite(site);
        attendance.setClockIn(LocalDateTime.now());

        AttendanceLog saved = attendanceRepository.save(attendance);

        activeWorkerCacheService.save(
                ActiveWorkerCache.builder()
                        .workerId(worker.getId())
                        .workerName(worker.getName())
                        .designation(worker.getDesignation().name())
                        .siteId(site.getId())
                        .siteName(site.getSiteName())
                        .clockInTime(saved.getClockIn())
                        .build()
        );

        return AttendanceResponse.builder()
                .attendanceId(saved.getId())
                .workerId(worker.getId())
                .workerName(worker.getName())
                .siteId(site.getId())
                .siteName(site.getSiteName())
                .clockIn(saved.getClockIn())
                .message("Worker clocked in successfully")
                .build();
    }

    public List<ActiveWorkerCache> getActiveWorkers() {

        return activeWorkerCacheService.getAllActiveWorkers();
    }
}