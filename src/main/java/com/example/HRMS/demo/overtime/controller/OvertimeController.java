package com.example.HRMS.demo.overtime.controller;

import com.example.HRMS.demo.overtime.dto.OvertimeSummaryResponse;
import com.example.HRMS.demo.overtime.dto.SettlementResponse;
import com.example.HRMS.demo.overtime.service.OvertimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/overtime")
@RequiredArgsConstructor
public class OvertimeController {

    private final OvertimeService overtimeService;

    @GetMapping("/summary/{workerId}")
    public OvertimeSummaryResponse getSummary(
            @PathVariable Long workerId,
            @RequestParam String month
    ) {

        return overtimeService.getMonthlySummary(
                workerId,
                month
        );
    }

    @PostMapping("/settle/{workerId}")
    public SettlementResponse settleOvertime(

            @PathVariable Long workerId,

            @RequestParam String month
    ) {

        return overtimeService.settleOvertime(
                workerId,
                month
        );
    }
}