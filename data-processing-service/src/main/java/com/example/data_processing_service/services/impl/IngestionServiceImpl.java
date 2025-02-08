package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.client.IngestionClient;
import com.example.data_processing_service.models.RawMarketModel;
import com.example.data_processing_service.services.IngestionService;
import com.example.data_processing_service.services.exception.IngestionException;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class IngestionServiceImpl implements IngestionService {
    private final IngestionClient ingestionClient;

    private static final Integer EXPECTED_MARKET_SIZE = 100;

    @Nonnull
    @Override
    public Set<RawMarketModel> fetchRawMarkets(@Nonnull Long timestamp) throws IllegalArgumentException {
        log.info("Fetching raw markets for timestamp: {}", timestamp);
        try {
            Set<RawMarketModel> rawMarketModels = ingestionClient.getRawMarketModels(timestamp);
            validateMarkets(rawMarketModels);

            log.info("Successfully fetched {} markets for timestamp: {}", rawMarketModels.size(), timestamp);
            return rawMarketModels;
        } catch (RestClientResponseException ex) {
            throw new IngestionException(String.format("HTTP error while fetching markets. Status: %s, Body: %s",
                    ex.getStatusCode(), ex.getResponseBodyAsString()), ex);
        } catch (IllegalArgumentException ex) {
            log.error("Market data validation failed for timestamp: {}", timestamp, ex);
            throw ex;
        } catch (Exception ex) {
            throw new IngestionException(String.format("Unexpected error while fetching markets for timestamp: %s", timestamp), ex);
        }
    }

    private void validateMarkets(@Nonnull Set<RawMarketModel> rawMarketModels) throws IllegalArgumentException {
        if (rawMarketModels.isEmpty()) {
            throw new IllegalArgumentException(
                    "Received empty market set from ingestion service. This might indicate an API issue"
            );
        }

        if (rawMarketModels.size() != EXPECTED_MARKET_SIZE) {
            throw new IllegalArgumentException(String.format(
                    "Expected %d markets but received %d. This indicates incomplete data from the data-ingestion microservice",
                    EXPECTED_MARKET_SIZE,
                    rawMarketModels.size()
            ));
        }
    }
}
