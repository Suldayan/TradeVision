package com.example.data_ingestion_service.services.scheduler;

import com.example.data_ingestion_service.services.DataAsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/*
* Orchestrates the scheduling for the data fetches by activating the data aggregate service every 5 minutes
* */

@Component
@Slf4j
@RequiredArgsConstructor
public class DataScheduler {
    private final DataAsyncService dataAsyncService;

    // 5 minute scheduling
    @Scheduled(fixedRateString = "300000")
    public void scheduler() {
        dataAsyncService.asyncFetch()
                .exceptionally(error -> {
                    log.error("Scheduled fetch failed: {}", error.getMessage(), error);
                    return null;
                });
    }
}
