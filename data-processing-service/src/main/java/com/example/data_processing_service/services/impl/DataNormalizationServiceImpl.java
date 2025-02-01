package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.client.IngestionClient;
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
    private final IngestionClient ingestionClient;

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    @Nonnull
    @Override
    public Set<MarketModel> transformToMarketModel(@Nonnull Long timestamp) throws DataNotFoundException {
        // The timestamp will be served via data-ingestion-status topic
        // TODO configure this to fetch from the data ingestion rest api rather than the repo
        Set<RawMarketModel> rawMarketModels = ingestionClient.getRawMarketModels(timestamp);
        if (rawMarketModels.isEmpty()) {
            log.error("Market models fetched but is empty for timestamp: {}", timestamp);
            // We throw an exception here because it's expected that there is data available at the given timestamp
            throw new DataNotFoundException("Unable to push data forward due to empty market set");
        }
        if (rawMarketModels.size() != 100) {
            log.error("Market models with timestamp: {} fetched but is missing data with size: {} of expected size: 100",
                    timestamp, rawMarketModels.size());
            throw new DataNotFoundException("Unable to push data forward due to missing data");
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
        return TIMESTAMP_FORMATTER.format(instant);
    }
}
