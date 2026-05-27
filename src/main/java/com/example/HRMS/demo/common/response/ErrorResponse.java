package com.example.HRMS.demo.common.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ErrorResponse {

    private String error;

    private String message;

    private Instant timestamp;
}