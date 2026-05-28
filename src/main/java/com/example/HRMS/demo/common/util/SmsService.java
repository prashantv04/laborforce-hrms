package com.example.HRMS.demo.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsService {

    public void sendSettlementSms(
            String phone,
            String workerName,
            String month,
            String amount
    ) {

        log.info("""
                SMS SENT
                To: {}
                Worker: {}
                Month: {}
                Amount: {}
                """,
                phone,
                workerName,
                month,
                amount
        );
    }
}