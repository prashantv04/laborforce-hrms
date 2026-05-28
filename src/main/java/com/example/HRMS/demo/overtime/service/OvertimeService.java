package com.example.HRMS.demo.overtime.service;

import com.example.HRMS.demo.common.exception.ResourceNotFoundException;
import com.example.HRMS.demo.overtime.dto.OvertimeEntryResponse;
import com.example.HRMS.demo.overtime.dto.OvertimeSummaryResponse;
import com.example.HRMS.demo.overtime.entity.OvertimeEntry;
import com.example.HRMS.demo.overtime.entity.SettlementStatus;
import com.example.HRMS.demo.overtime.repository.OvertimeRepository;
import com.example.HRMS.demo.worker.entity.Worker;
import com.example.HRMS.demo.worker.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OvertimeService {

    private final OvertimeRepository overtimeRepository;

    private final WorkerRepository workerRepository;

    public OvertimeSummaryResponse getMonthlySummary(
            Long workerId,
            String month
    ) {

        Worker worker =
                workerRepository.findById(workerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Worker not found"
                                ));

        YearMonth yearMonth =
                YearMonth.parse(month);

        LocalDate startDate =
                yearMonth.atDay(1);

        LocalDate endDate =
                yearMonth.atEndOfMonth();

        List<OvertimeEntry> entries =
                overtimeRepository
                        .findByWorkerIdAndOvertimeDateBetween(
                                workerId,
                                startDate,
                                endDate
                        );

        BigDecimal totalHours =
                entries.stream()
                        .map(OvertimeEntry::getOvertimeHours)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmount =
                entries.stream()
                        .map(OvertimeEntry::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<OvertimeEntryResponse> responses =
                entries.stream()
                        .map(entry ->
                                OvertimeEntryResponse.builder()
                                        .date(entry.getOvertimeDate())
                                        .hours(entry.getOvertimeHours())
                                        .amount(entry.getAmount())
                                        .settlementStatus(
                                                entry.getSettlementStatus().name()
                                        )
                                        .build()
                        )
                        .toList();

        boolean allSettled =
                entries.stream()
                        .allMatch(entry ->
                                entry.getSettlementStatus()
                                        == SettlementStatus.SETTLED
                        );

        return OvertimeSummaryResponse.builder()
                .workerId(worker.getId())
                .workerName(worker.getName())
                .month(month)
                .totalOvertimeHours(totalHours)
                .totalAmount(totalAmount)
                .settlementStatus(
                        allSettled ? "SETTLED" : "PENDING"
                )
                .entries(responses)
                .build();
    }
}