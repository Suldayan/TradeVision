package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.client.IngestionClient;
import com.example.data_processing_service.models.RawMarketModel;
import com.example.data_processing_service.services.IngestionService;
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
    public Set<RawMarketModel> fetchRawMarkets(@Nonnull Long timestamp) {
        try {
            Set<RawMarketModel> rawMarketModels = ingestionClient.getRawMarketModels(timestamp);
            validateMarkets(rawMarketModels);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    private void validateMarkets(@Nonnull Set<RawMarketModel> rawMarketModels) {
        if (rawMarketModels.isEmpty()) {
            
        }
        if (rawMarketModels.size() != 100) {
            log.error("");
        }
    }
}
