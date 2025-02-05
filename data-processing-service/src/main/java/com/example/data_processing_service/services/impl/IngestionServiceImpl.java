package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.client.IngestionClient;
import com.example.data_processing_service.models.RawMarketModel;
import com.example.data_processing_service.services.IngestionService;
import com.example.data_processing_service.services.exception.DataValidationException;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class IngestionServiceImpl implements IngestionService {
    private final IngestionClient ingestionClient;

    @Nonnull
    @Override
    public Set<RawMarketModel> fetchRawMarkets(@Nonnull Long timestamp) throws DataValidationException {
        try {
            Set<RawMarketModel> rawMarketModels = ingestionClient.getRawMarketModels(timestamp);
            validateMarkets(rawMarketModels);
            return rawMarketModels;
        } catch (DataValidationException ex) {
            throw new DataValidationException("");
        }
    }

    private void validateMarkets(@Nonnull Set<RawMarketModel> rawMarketModels) throws DataValidationException {
        if (rawMarketModels.isEmpty()) {
            throw new DataValidationException("Market set is empty. Ingestion must have had an API issue");
        }
        if (rawMarketModels.size() != 100) {
            throw new DataValidationException("Market set is not 100. Ingestion microservice may have returned incomplete API data");
        }
    }
}
