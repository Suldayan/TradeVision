package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.models.processed.MarketModel;
import com.example.data_processing_service.repository.processed.MarketModelRepository;
import com.example.data_processing_service.services.DataPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataPersistenceServiceImpl implements DataPersistenceService {
    private final MarketModelRepository marketModelRepository;

    @Retryable(
            backoff = @Backoff(delay = 1000)
    )
    @Transactional
    @Override
    public void saveToDatabase(Set<MarketModel> marketModels) {
        try {
            marketModelRepository.saveAll(marketModels);
        } catch (Exception ex) {
            log.error("Failed to save to database at: {}", LocalTime.now());
        }
    }
}
