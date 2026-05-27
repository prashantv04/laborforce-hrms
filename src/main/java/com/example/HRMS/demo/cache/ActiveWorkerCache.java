package com.example.HRMS.demo.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveWorkerCache implements Serializable {

    private Long workerId;

    private String workerName;

    private String designation;

    private Long siteId;

    private String siteName;

    private LocalDateTime clockInTime;
}