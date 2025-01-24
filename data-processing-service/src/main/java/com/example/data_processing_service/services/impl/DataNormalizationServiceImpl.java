package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.models.processed.MarketModel;
import com.example.data_processing_service.models.raw.RawMarketModel;
import com.example.data_processing_service.repository.raw.RawMarketModelRepository;
import com.example.data_processing_service.services.DataNormalizationService;
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
    public Set<MarketModel> removeFields() {
        // The timestamp will be served via status topic from kafka from the data ingestion service
        Set<RawMarketModel> rawMarketModels = repository.findAllByTimestamp();
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
