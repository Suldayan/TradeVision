package com.example.trade_vision_backend.ingestion.application.scheduler;

import com.example.trade_vision_backend.ingestion.core.service.IngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class IngestionScheduler {
    private final IngestionService ingestionService;

    @Scheduled(fixedRate = 900000)
    public void scheduleMarketDataIngestion() {
        log.info("Starting scheduled market ingestion");
        try {
            ingestionService.executeIngestion();
        } catch (RestClientException ex) {
            log.warn("Failed to fetch market data: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error during scheduled market ingestion: ", ex);
        }
    }
}
