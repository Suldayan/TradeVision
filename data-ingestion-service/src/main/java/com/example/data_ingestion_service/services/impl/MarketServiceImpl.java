package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.clients.MarketClient;
import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.records.Market;
import com.example.data_ingestion_service.records.wrapper.MarketWrapper;
import com.example.data_ingestion_service.services.MarketService;
import com.example.data_ingestion_service.services.exceptions.ApiException;
import com.example.data_ingestion_service.services.mapper.MarketMapper;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketServiceImpl implements MarketService {
    private final MarketClient marketClient;
    private final MarketMapper marketMapper;

    @Nonnull
    @Override
    public Set<Market> getMarketsData() throws ApiException {
        try {
            MarketWrapper marketHolder = marketClient.getMarkets();
            if (marketHolder == null) {
                log.error("Fetched data from market wrapper returned as null");
                throw new ApiException("Markets data fetched but return as null");
            }
            Set<Market> marketSet = marketHolder.markets();
            if (marketSet.isEmpty()) {
                log.warn("Market set returned as empty. Endpoint might be returning incomplete data");
                throw new ApiException("Market set fetched but is empty");
            }
            log.info("Successfully fetched {} markets.", marketSet.size());
            return marketSet;
        } catch (Exception e) {
            log.error("An error occurred while fetching markets data: {}", e.getMessage());
            throw new ApiException(String.format("Failed to fetch market wrapper data: %s", e));
        }
    }

    @Nonnull
    @Override
    public Set<RawMarketModel> convertToModel() {
        return marketMapper.marketRecordToEntity(getMarketsData());
    }
}
