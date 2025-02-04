package com.example.data_processing_service.kafka.consumer;

import com.example.data_processing_service.dto.EventDTO;
import com.example.data_processing_service.services.ProcessingOrchestratorService;
import com.example.data_processing_service.services.exception.EventProcessingException;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class StatusConsumer {
    private final ProcessingOrchestratorService orchestratorService;

    private static final String TOPIC = "status";
    private static final String GROUP = "processing";

    @KafkaListener(
            topics = TOPIC,
            groupId = GROUP
    )
    public void receiveStatus(@Nonnull EventDTO status) {
        try {
            orchestratorService.startProcessingFlow(status.getTimestamp());
        } catch (Exception e) {
            log.error("Error initiating orchestration service at: {} for data fetched at: {}, {}",
                    LocalDateTime.now(), status.getTimestamp(), e.getMessage());
            throw new EventProcessingException("Unexpected error occurred while receiving event status", status.getTimestamp(), e);
        }
    }
}
