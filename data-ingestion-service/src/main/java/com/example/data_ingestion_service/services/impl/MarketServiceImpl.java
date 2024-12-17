package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.clients.MarketClient;
import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.models.RawMarketWrapperModel;
import com.example.data_ingestion_service.services.MarketService;
import com.example.data_ingestion_service.services.exceptions.ApiException;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketServiceImpl implements MarketService {
    private final MarketClient marketClient;

    @Nonnull
    @Override
    public RawMarketWrapperModel getMarketData() {
        try {
            RawMarketWrapperModel markets = marketClient.getMarkets();
            log.info("Fetched markets with result: {}", markets);
            if (markets == null) {
                log.error("Fetched data from market wrapper returned as null");
                throw new ApiException("Markets data fetched but return as null");
            }
            return markets;
        } catch (Exception e) {
            log.error("An error occurred while fetching markets data");
            throw new ApiException("Failed to fetch market wrapper data");
        }
    }

    @Nonnull
    @Override
    public List<RawMarketModel> getMarketsAsList() {
        try {
            List<RawMarketModel> market = marketClient.getMarkets().getMarketModelList();
            if (market == null) {
                log.error("Data retrieved from markets but returned as null");
                throw new ApiException("Market list retrieved but returned as null");
            }
            return market;
        } catch (Exception e) {
            log.error("An error occurred while fetching market list");
            throw new ApiException("Failed to retrieve market list");
        }
    }
}
