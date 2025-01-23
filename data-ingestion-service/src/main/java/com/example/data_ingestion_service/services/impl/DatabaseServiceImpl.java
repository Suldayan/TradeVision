package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.repository.RawMarketModelRepository;
import com.example.data_ingestion_service.services.DatabaseService;
import com.example.data_ingestion_service.services.producer.KafkaProducer;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatabaseServiceImpl implements DatabaseService {
    private final RawMarketModelRepository marketModelRepository;
    private final KafkaProducer kafkaProducer;

    @Transactional
    @Override
    public void saveToDatabase(@Nonnull Set<RawMarketModel> marketModels) {
        if (marketModels.isEmpty()) {
            log.warn("The list of entities for saving has been passed but is empty");
        }
        marketModelRepository.save(marketModels);
        // Send completion status to data processing microservice to initialize the processing flow
        kafkaProducer.sendMessage(String.format("Status: Completed at %s", LocalTime.now()));
    }
}
