package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.clients.ExchangeClient;
import com.example.data_ingestion_service.models.RawExchangesModel;
import com.example.data_ingestion_service.records.Exchange;
import com.example.data_ingestion_service.records.wrapper.ExchangeWrapper;
import com.example.data_ingestion_service.services.ExchangeService;
import com.example.data_ingestion_service.services.exceptions.ApiException;
import com.example.data_ingestion_service.services.mapper.ExchangeMapper;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {
    private final ExchangeClient exchangeClient;
    private final ExchangeMapper exchangeMapper;

    @Nonnull
    @Override
    public Set<Exchange> getExchangeData() throws ApiException {
        try {
            ExchangeWrapper exchangeHolder = exchangeClient.getExchanges();
            if (exchangeHolder == null) {
                throw new ApiException("Exchanges data fetched but returned as null");
            }
            Set<Exchange> exchangeSet = exchangeHolder.exchanges()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(field -> Exchange.builder()
                            .exchangeId(field.exchangeId())
                            .name(field.name())
                            .rank(field.rank())
                            .percentTotalVolume(field.percentTotalVolume())
                            .volumeUsd(field.volumeUsd())
                            .tradingPairs(field.tradingPairs())
                            .socket(field.socket())
                            .exchangeUrl(field.exchangeUrl())
                            .updated(field.updated())
                            .build())
                    .collect(Collectors.toSet());
            if (exchangeSet.isEmpty()) {
                log.warn("Exchange set fetched as empty. Endpoint might be returning incomplete data");
                throw new ApiException("Exchange set fetched but is empty");
            }
            log.info("Successfully fetched {} exchanges.", exchangeSet.size());
            return exchangeSet;
        } catch (Exception e) {
            log.error("An error occurred while fetching exchanges data: {}", e.getMessage());
            throw new ApiException(String.format("Failed to fetch exchange wrapper data: %s", e));
        }
    }

    @Nonnull
    @Override
    public Set<RawExchangesModel> convertToModel() {
        return exchangeMapper.exchangeRecordToEntity(getExchangeData());
    }
}
