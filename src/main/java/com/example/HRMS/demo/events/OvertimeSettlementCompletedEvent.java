package com.example.HRMS.demo.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class OvertimeSettlementCompletedEvent {

    private Long workerId;

    private String workerName;

    private String phoneNumber;

    private String month;

    private BigDecimal amount;
}