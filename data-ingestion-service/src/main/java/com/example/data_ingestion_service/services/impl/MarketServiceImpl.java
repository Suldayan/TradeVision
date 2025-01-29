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

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketServiceImpl implements MarketService {
    private final MarketClient marketClient;
    private final MarketMapper marketMapper;

    @Nonnull
    @Override
    public MarketWrapper getMarketsData() {
        try {
            MarketWrapper marketHolder = marketClient.getMarkets();
            validateMarketWrapper(marketHolder);
            return marketHolder;
        } catch (Exception e) {
            log.error("An error occurred while fetching markets data: {}", e.getMessage());
            throw new ApiException(String.format("Failed to fetch market wrapper data: %s", e));
        }
    }

    @Nonnull
    @Override
    public Set<Market> convertWrapperDataToRecord(@Nonnull MarketWrapper data) throws ApiException {
        Set<Market> marketSet = data.markets()
                .stream()
                .filter(Objects::nonNull)
                .map(field -> Market.builder()
                        .exchangeId(field.exchangeId())
                        .rank(field.rank())
                        .baseSymbol(field.baseSymbol())
                        .baseId(field.baseId())
                        .quoteSymbol(field.quoteSymbol())
                        .quoteId(field.quoteId())
                        .priceQuote(field.priceQuote())
                        .priceUsd(field.priceUsd())
                        .volumeUsd24Hr(field.volumeUsd24Hr())
                        .percentExchangeVolume(field.percentExchangeVolume())
                        .tradesCount24Hr(field.tradesCount24Hr())
                        .updated(field.updated())
                        .timestamp(data.timestamp())
                        .build())
                .collect(Collectors.toSet());
        if (marketSet.isEmpty()) {
            log.warn("Market set returned as empty. Endpoint might be returning incomplete data");
            throw new ApiException("Market set fetched but is empty");
        }
        log.info("Successfully fetched {} markets.", marketSet.size());
        return marketSet;
    }

    @Nonnull
    @Override
    public Set<RawMarketModel> convertToModel(@Nonnull Set<Market> marketRecords) {
        return marketMapper.marketRecordToEntity(marketRecords);
    }

    private void validateMarketWrapper(@Nonnull MarketWrapper marketHolder) {
        if (marketHolder.markets().isEmpty()) {
            throw new ApiException("Market set from wrapper returned empty");
        }
        if (marketHolder.markets().stream().anyMatch(Objects::isNull)) {
            throw new ApiException("An object in the market set has returned as null");
        }
    }
}
