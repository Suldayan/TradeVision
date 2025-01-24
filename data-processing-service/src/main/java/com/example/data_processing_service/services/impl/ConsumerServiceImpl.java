package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.services.ConsumerService;
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

    //TODO configure group id
    @KafkaListener(topics = "status", groupId = "")
    @Override
    public void receiveStatus(@Nonnull String status) {
        log.info("Status completion received at: {}", LocalTime.now());
    }

    @Nonnull
    @Override
    public Long retrieveTimestamp() {

    }
}
