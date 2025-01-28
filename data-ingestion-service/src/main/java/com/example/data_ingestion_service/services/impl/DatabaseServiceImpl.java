package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.repository.RawMarketModelRepository;
import com.example.data_ingestion_service.services.DatabaseService;
import com.example.data_ingestion_service.services.exceptions.DatabaseException;
import com.example.data_ingestion_service.services.producer.KafkaProducer;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatabaseServiceImpl implements DatabaseService {
    private final RawMarketModelRepository marketModelRepository;

    @Transactional
    @Override
    public void saveToDatabase(@Nonnull Set<RawMarketModel> marketModels) throws DatabaseException {
        if (marketModels.isEmpty()) {
            log.warn("The list of entities for saving has been passed but is empty");
        }
        try {
            marketModelRepository.saveAll(marketModels);
        } catch (Exception e) {
            throw new DatabaseException("", e);
        }
    }
}
