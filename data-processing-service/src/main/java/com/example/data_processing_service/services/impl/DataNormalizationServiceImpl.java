package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.models.MarketModel;
import com.example.data_processing_service.models.RawMarketModel;
import com.example.data_processing_service.repository.RawMarketModelRepository;
import com.example.data_processing_service.services.DataNormalizationService;
import com.example.data_processing_service.services.exception.DataNotFoundException;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataNormalizationServiceImpl implements DataNormalizationService {
    private final RawMarketModelRepository repository;

    @Nonnull
    @Override
    public Set<MarketModel> removeFields(@Nonnull Long timestamp) throws DataNotFoundException {
        // The timestamp will be served via status topic from kafka from the data ingestion service
        Set<RawMarketModel> rawMarketModels = repository.findAllByTimestamp(timestamp);
        if (rawMarketModels.isEmpty()) {
            log.error("Market models fetched at: {} returned empty", timestamp);
            throw new DataNotFoundException(String.format("Market Models returned empty at timestamp: %s", timestamp));
        }
        return rawMarketModels.stream()
                .map(field -> MarketModel
                        .builder()
                        .priceUsd(field.getPriceUsd())
                        .updated(field.getUpdated())
                        .timestamp(transformTimeStamp(field.getTimestamp()))
                        .build())
                .collect(Collectors.toSet());
    }

    @Nonnull
    @Override
    public String transformTimeStamp(@Nonnull Long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        return formatter.format(instant);
    }
}
