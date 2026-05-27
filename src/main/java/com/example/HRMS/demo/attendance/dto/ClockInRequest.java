package com.example.HRMS.demo.attendance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClockInRequest {

    @NotNull(message = "Worker ID is required")
    private Long workerId;

    @NotNull(message = "Site ID is required")
    private Long siteId;
}