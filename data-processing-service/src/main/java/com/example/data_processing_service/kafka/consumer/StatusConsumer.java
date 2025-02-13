package com.example.data_processing_service.kafka.consumer;

import com.example.data_processing_service.kafka.dto.EventDTO;
import com.example.data_processing_service.features.orchestrator.service.OrchestratorService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class StatusConsumer {
    private final OrchestratorService orchestratorService;

    private static final String TOPIC = "status";
    private static final String GROUP = "processing";

    @KafkaListener(
            topics = TOPIC,
            groupId = GROUP
    )
    public void receiveStatus(@Nonnull EventDTO status) {
        try {
            log.info("Status: {}, now starting orchestrator", status);
            orchestratorService.startProcessingFlow(status.getTimestamp());
        } catch (Exception ex) {
            log.error("Error initiating orchestration service at: {} for data fetched at: {}, {}",
                    LocalDateTime.now(), status.getTimestamp(), ex.getMessage());
            throw new KafkaException("Unexpected error occurred while receiving event status", ex);
        }
    }
}