package com.example.data_processing_service.features.processing.service.impl;

import com.example.data_processing_service.database.model.MarketModel;
import com.example.data_processing_service.features.shared.dto.RawMarketDTO;
import com.example.data_processing_service.features.processing.service.DataNormalizationService;
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

    @Nonnull
    @Override
    public Set<MarketModel> transformToMarketModel(
            @Nonnull Set<RawMarketDTO> rawMarketModels,
            @Nonnull Long timestamp)
            throws IllegalArgumentException {
        try {
            validateRawMarketModels(rawMarketModels, timestamp);

            log.debug("Successfully validated models");
            return rawMarketModels.stream()
                    .map(field -> MarketModel
                            .builder()
                            .baseId(field.getBaseId())
                            .quoteId(field.getQuoteId())
                            .exchangeId(field.getExchangeId())
                            .priceUsd(field.getPriceUsd())
                            .updated(field.getUpdated())
                            .timestamp(transformTimestamp(field.getTimestamp()))
                            .build())
                    .collect(Collectors.toSet());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(String.format("Failed to transform model from raw to processed for timestamped data: %s",
                    timestamp), ex);
        }
    }

    @Nonnull
    private ZonedDateTime transformTimestamp(@Nonnull Long timestamp) {
        return ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneOffset.UTC
        );
    }

    private void validateRawMarketModels(
            @Nonnull Set<RawMarketDTO> rawMarketModels,
            @Nonnull Long timestamp) throws IllegalArgumentException {
        if (rawMarketModels.isEmpty()) {
            // We throw an exception here because it's expected that there is data available at the given timestamp
            throw new IllegalArgumentException(String.format("Unable to push data forward due to empty market set for timestamp: %s", timestamp));
        }
        if (rawMarketModels.size() != 100) {
            throw new IllegalArgumentException(String.format("Market models with timestamp: %s fetched but is missing data with size: %s of expected size: 100",
                    timestamp, rawMarketModels.size()));
        }
    }
}
