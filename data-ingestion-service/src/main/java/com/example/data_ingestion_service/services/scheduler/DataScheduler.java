package com.example.data_ingestion_service.services.scheduler;

import com.example.data_ingestion_service.services.DataAggregateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataScheduler {
    private final DataAggregateService dataAggregateService;

    // 5 minute scheduling
    @Scheduled(fixedRateString = "300000")
    public void scheduler() {
        dataAggregateService.asyncFetch()
                .exceptionally(error -> {
                    log.error("Scheduled fetch failed: {}", error.getMessage(), error);
                    return null;
                });
    }
}
