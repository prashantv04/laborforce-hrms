package com.example.HRMS.demo.listener;

import com.example.HRMS.demo.common.util.SmsService;
import com.example.HRMS.demo.events.OvertimeSettlementCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementNotificationListener {

    private final SmsService smsService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleSettlementCompleted(
            OvertimeSettlementCompletedEvent event
    ) {

        try {

            smsService.sendSettlementSms(
                    event.getPhoneNumber(),
                    event.getWorkerName(),
                    event.getMonth(),
                    event.getAmount().toString()
            );

        } catch (Exception ex) {

            log.error(
                    "Failed to send settlement SMS for worker {}",
                    event.getWorkerId(),
                    ex
            );
        }
    }
}