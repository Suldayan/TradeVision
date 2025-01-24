package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.dto.EventDTO;
import com.example.data_processing_service.services.ConsumerService;
import com.example.data_processing_service.services.DataNormalizationService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {
    private final DataNormalizationService dataNormalizationService;

    //TODO configure proper group id
    @KafkaListener(topics = "status", groupId = "")
    @Override
    public void receiveStatus(@Nonnull EventDTO status) {
        log.info("Status completion received at: {}", LocalTime.now());
        dataNormalizationService.removeFields(status.getTimestamp());
    }
}
