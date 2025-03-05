package com.example.trade_vision_backend.processing.internal.infrastructure.service;

import com.example.trade_vision_backend.ingestion.market.RawMarketModel;
import com.example.trade_vision_backend.processing.ProcessedMarketModel;
import com.example.trade_vision_backend.processing.internal.infrastructure.db.ProcessingRepository;
import com.example.trade_vision_backend.processing.internal.infrastructure.exception.ProcessingException;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessingServiceImpl implements ProcessingService {
    private final ProcessingRepository processingRepository;

    @Nonnull
    @Override
    public Set<ProcessedMarketModel> transformToMarketModel(
            @Nonnull Set<RawMarketModel> rawMarketModels,
            @Nonnull Long timestamp
    ) throws IllegalArgumentException {
        try {
            return rawMarketModels.stream()
                    .map(field -> {
                        assert field.getTimestamp() != null;
                        return ProcessedMarketModel.builder()
                                .baseId(field.getBaseId())
                                .quoteId(field.getQuoteId())
                                .exchangeId(field.getExchangeId())
                                .priceUsd(field.getPriceUsd())
                                .updated(field.getUpdated())
                                .timestamp(transformTimestamp(field.getTimestamp()))
                                .build();
                    })
                    .collect(Collectors.toSet());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(String.format("Failed to transform model from raw to processed for timestamped data: %s",
                    timestamp), ex);
        }
    }

    @Override
    public void executeProcessing(
            @Nonnull Set<RawMarketModel> rawMarketModels,
            @Nonnull Long timestamp
    ) throws ProcessingException {
        try {
            validateRawMarketModels(rawMarketModels, timestamp);
            Set<ProcessedMarketModel> processedData = transformToMarketModel(rawMarketModels, timestamp);
            saveProcessedData(processedData);
        } catch (Exception ex) {
            throw new ProcessingException("", ex);
        }
    }

    @Transactional
    @Override
    public void saveProcessedData(Set<ProcessedMarketModel> processedData) throws ProcessingException {
        try {
            processingRepository.saveAll(processedData);
        } catch (DataAccessException ex) {
            throw new ProcessingException("", ex);
        } catch (Exception ex) {
            throw new ProcessingException("Unexpected error occurred while saving processed data", ex);
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
            @Nonnull Set<RawMarketModel> rawMarketModels,
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
