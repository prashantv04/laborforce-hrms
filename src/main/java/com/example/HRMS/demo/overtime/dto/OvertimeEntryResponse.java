package com.example.HRMS.demo.overtime.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class OvertimeEntryResponse {

    private LocalDate date;

    private BigDecimal hours;

    private BigDecimal amount;

    private String settlementStatus;
}