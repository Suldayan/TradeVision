package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.dto.EventDTO;
import com.example.data_processing_service.services.ConsumerService;
import com.example.data_processing_service.services.ProcessingOrchestratorService;
import com.example.data_processing_service.services.exception.EventProcessingException;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {
    private final ProcessingOrchestratorService processingOrchestratorService;

    @KafkaListener(
            topics = "${kafka.topics.data-ingestion-status:data-ingestion-status}",
            groupId = "${kafka.consumer-group:myGroup}"
    )
    @Override
    public void receiveStatus(@Nonnull EventDTO status) {
        try {
            log.info("{} received at: {}. Starting processing",
                    status.getStatus(), LocalDateTime.now());
            processingOrchestratorService.startProcessingFlow(status.getTimestamp());
        } catch (Exception e) {
            log.error("Error initiating orchestration service at: {} for data fetched at: {}, {}",
                    LocalDateTime.now(), status.getTimestamp(), e.getMessage());
            throw new EventProcessingException("Unexpected error occurred while receiving event status", status.getTimestamp(), e);
        }
    }
}
