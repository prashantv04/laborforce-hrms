package com.example.HRMS.demo.overtime.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class OvertimeSummaryResponse {

    private Long workerId;

    private String workerName;

    private String month;

    private BigDecimal totalOvertimeHours;

    private BigDecimal totalAmount;

    private String settlementStatus;

    private List<OvertimeEntryResponse> entries;
}