package com.example.HRMS.demo.attendance.entity;

import com.example.HRMS.demo.common.util.BaseEntity;
import com.example.HRMS.demo.site.entity.Site;
import com.example.HRMS.demo.worker.entity.Worker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "attendance_logs",
        indexes = {
                @Index(name = "idx_attendance_worker", columnList = "worker_id"),
                @Index(name = "idx_attendance_clockin", columnList = "clock_in")
        }
)
public class AttendanceLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(name = "clock_in", nullable = false)
    private LocalDateTime clockIn;

    @Column(name = "clock_out")
    private LocalDateTime clockOut;

    @Column(name = "total_hours", precision = 5, scale = 2)
    private BigDecimal totalHours;

    @Column(name = "overtime_hours", precision = 5, scale = 2)
    private BigDecimal overtimeHours;

    @Column(nullable = false)
    private Boolean flagged = false;
}