package com.example.HRMS.demo.overtime.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class SettlementResponse {

    private Long workerId;

    private String workerName;

    private String month;

    private BigDecimal totalAmount;

    private String message;
}