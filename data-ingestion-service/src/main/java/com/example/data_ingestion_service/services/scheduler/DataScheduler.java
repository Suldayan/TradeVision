package com.example.data_ingestion_service.services.scheduler;

import com.example.data_ingestion_service.services.OrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/*
* Orchestrates the scheduling for the data fetches by activating the data aggregate service every 5 minutes
* */

@Component
@Slf4j
@RequiredArgsConstructor
public class DataScheduler {
    private final OrchestratorService orchestratorService;

    // 5-minute cron job
    @Scheduled(cron = "0 */5 * * * *")
    public void scheduler() {
        try {
            log.info("Starting scheduled fetch at: {}", LocalTime.now());
            orchestratorService.executeDataPipeline();
            log.info("Scheduled fetch completed successfully");
        } catch (Exception ex) {
            log.error("Failed to start scheduled task: {}", ex.getMessage(), ex);
        }
    }
}
