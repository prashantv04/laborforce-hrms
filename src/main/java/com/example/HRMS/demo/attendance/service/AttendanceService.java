package com.example.HRMS.demo.attendance.service;

import com.example.HRMS.demo.attendance.dto.AttendanceResponse;
import com.example.HRMS.demo.attendance.dto.ClockInRequest;
import com.example.HRMS.demo.attendance.dto.ClockOutRequest;
import com.example.HRMS.demo.attendance.entity.AttendanceLog;
import com.example.HRMS.demo.attendance.repository.AttendanceRepository;
import com.example.HRMS.demo.cache.ActiveWorkerCache;
import com.example.HRMS.demo.cache.ActiveWorkerCacheService;
import com.example.HRMS.demo.common.exception.ConflictException;
import com.example.HRMS.demo.common.exception.ResourceNotFoundException;
import com.example.HRMS.demo.common.exception.ValidationException;
import com.example.HRMS.demo.overtime.entity.OvertimeEntry;
import com.example.HRMS.demo.overtime.repository.OvertimeRepository;
import com.example.HRMS.demo.site.entity.Site;
import com.example.HRMS.demo.site.repository.SiteRepository;
import com.example.HRMS.demo.worker.entity.Worker;
import com.example.HRMS.demo.worker.repository.WorkerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    private final WorkerRepository workerRepository;

    private final SiteRepository siteRepository;

    private final OvertimeRepository overtimeRepository;

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

    @Transactional
    public AttendanceResponse clockOut(ClockOutRequest request) {

        AttendanceLog attendance =
                attendanceRepository
                        .findByWorkerIdAndClockOutIsNull(
                                request.getWorkerId()
                        )
                        .orElseThrow(() ->
                                new ValidationException(
                                        "Worker is not clocked in"
                                ));

        LocalDateTime now = LocalDateTime.now();

        attendance.setClockOut(now);

        long minutesWorked =
                Duration.between(
                        attendance.getClockIn(),
                        now
                ).toMinutes();

        BigDecimal totalHours =
                BigDecimal.valueOf(minutesWorked)
                        .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        attendance.setTotalHours(totalHours);

        BigDecimal overtimeHours = BigDecimal.ZERO;

        if (totalHours.compareTo(BigDecimal.valueOf(8)) > 0) {

            overtimeHours =
                    totalHours.subtract(BigDecimal.valueOf(8));

            attendance.setOvertimeHours(overtimeHours);

            processOvertime(attendance, overtimeHours);
        }
        else {
            attendance.setOvertimeHours(BigDecimal.ZERO);
        }

        if (totalHours.compareTo(BigDecimal.valueOf(16)) > 0) {
            attendance.setFlagged(true);
        }

        AttendanceLog saved =
                attendanceRepository.save(attendance);

        activeWorkerCacheService.remove(
                attendance.getWorker().getId()
        );

        return AttendanceResponse.builder()
                .attendanceId(saved.getId())
                .workerId(saved.getWorker().getId())
                .workerName(saved.getWorker().getName())
                .siteId(saved.getSite().getId())
                .siteName(saved.getSite().getSiteName())
                .clockIn(saved.getClockIn())
                .message("Worker clocked out successfully")
                .build();
    }

    public List<ActiveWorkerCache> getActiveWorkers() {

        return activeWorkerCacheService.getAllActiveWorkers();
    }

    private void processOvertime(
            AttendanceLog attendance,
            BigDecimal overtimeHours
    ) {

        Worker worker = attendance.getWorker();

        LocalDate overtimeDate =
                attendance.getClockIn().toLocalDate();

        YearMonth yearMonth =
                YearMonth.from(overtimeDate);

        BigDecimal existingMonthlyHours =
                overtimeRepository.getMonthlyOvertimeHours(
                        worker.getId(),
                        yearMonth.atDay(1),
                        yearMonth.atEndOfMonth()
                );

        BigDecimal remainingCap =
                BigDecimal.valueOf(60)
                        .subtract(existingMonthlyHours);

        if (remainingCap.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal cappedHours =
                overtimeHours.min(remainingCap);

        BigDecimal wage = worker.getDailyWageRate();

        BigDecimal amount = calculateOvertimeAmount(
                wage,
                cappedHours
        );

        OvertimeEntry overtimeEntry =
                new OvertimeEntry();

        overtimeEntry.setWorker(worker);
        overtimeEntry.setAttendanceLog(attendance);
        overtimeEntry.setOvertimeDate(overtimeDate);
        overtimeEntry.setOvertimeHours(cappedHours);
        overtimeEntry.setOvertimeRateApplied(
                BigDecimal.valueOf(1.5)
        );
        overtimeEntry.setAmount(amount);

        overtimeRepository.save(overtimeEntry);
    }

    private BigDecimal calculateOvertimeAmount(
            BigDecimal dailyWage,
            BigDecimal overtimeHours
    ) {

        BigDecimal hourlyRate =
                dailyWage.divide(
                        BigDecimal.valueOf(8),
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal firstSlabHours =
                overtimeHours.min(BigDecimal.valueOf(2));

        BigDecimal secondSlabHours =
                overtimeHours.subtract(firstSlabHours);

        if (secondSlabHours.compareTo(BigDecimal.ZERO) < 0) {
            secondSlabHours = BigDecimal.ZERO;
        }

        BigDecimal firstSlabAmount =
                firstSlabHours
                        .multiply(hourlyRate)
                        .multiply(BigDecimal.valueOf(1.5));

        BigDecimal secondSlabAmount =
                secondSlabHours
                        .multiply(hourlyRate)
                        .multiply(BigDecimal.valueOf(2));

        return firstSlabAmount
                .add(secondSlabAmount)
                .setScale(2, RoundingMode.HALF_UP);
    }
}