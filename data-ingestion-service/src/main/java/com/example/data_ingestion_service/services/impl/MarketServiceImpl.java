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

    /*
    * Grabs all the market data from the market endpoint
    * @returns a list of each model, containing each crypto id and its metadata
    * */
    @Nonnull
    @Override
    public List<RawMarketModel> getMarketsData() {
        try {
            RawMarketWrapperModel marketHolder = marketClient.getMarkets();
            log.info("Fetched markets with result: {}", marketHolder);
            if (marketHolder == null) {
                log.error("Fetched data from market wrapper returned as null");
                throw new ApiException("Markets data fetched but return as null");
            }
            return marketHolder.getMarketModelList();
        } catch (Exception e) {
            log.error("An error occurred while fetching markets data");
            throw new ApiException(String.format("Failed to fetch market wrapper data: %s", e));
        }
    }
}
