package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.models.MarketModel;
import com.example.data_processing_service.models.RawMarketModel;
import com.example.data_processing_service.services.DataNormalizationService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataNormalizationServiceImpl implements DataNormalizationService {

    private static final Integer EXPECTED_MARKET_SIZE = 100;

    @Nonnull
    @Override
    public Set<MarketModel> transformToMarketModel(
            @Nonnull Set<RawMarketModel> rawMarketModels,
            @Nonnull Long timestamp)
            throws IllegalArgumentException {
        try {
            validateRawMarketModels(rawMarketModels, timestamp);

            log.debug("Successfully validated models");
            return buildMarketModel(rawMarketModels);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(String.format("Failed to transform model from raw to processed for timestamped data: %s",
                    timestamp), ex);
        }
    }

    @Nonnull
    private Set<MarketModel> buildMarketModel(@Nonnull Set<RawMarketModel> rawMarketModels) {
        return rawMarketModels.stream()
                .map(field -> MarketModel.builder()
                        .baseId(field.getBaseId())
                        .quoteId(field.getQuoteId())
                        .priceUsd(field.getPriceUsd())
                        .updated(field.getUpdated())
                        .timestamp(transformTimestamp(field.getTimestamp()))
                        .build())
                .collect(Collectors.toSet());
    }

    @Nonnull
    private ZonedDateTime transformTimestamp(@Nonnull Long timestamp) {
        return ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneOffset.UTC
        );
    }

    private void validateRawMarketModels(
            @Nonnull Set<RawMarketModel> rawMarketModels,
            @Nonnull Long timestamp) throws IllegalArgumentException {
        if (rawMarketModels.isEmpty()) {
            // We throw an exception here because it's expected that there is data available at the given timestamp
            throw new IllegalArgumentException(String.format("Unable to push data forward due to empty market set for timestamp: %s", timestamp));
        }
        if (rawMarketModels.size() != EXPECTED_MARKET_SIZE) {
            throw new IllegalArgumentException(String.format("Market models with timestamp: %s fetched but is missing data with size: %s of expected size: 100",
                    timestamp, rawMarketModels.size()));
        }
    }
}
