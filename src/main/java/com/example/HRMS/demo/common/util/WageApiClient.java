package com.example.HRMS.demo.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WageApiClient {

    public String fetchLatestWageRates() {

        try {

            Thread.sleep(3000);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }

        log.info("Fetched wage rates from external API");

        return "LATEST_WAGE_RATE";
    }
}