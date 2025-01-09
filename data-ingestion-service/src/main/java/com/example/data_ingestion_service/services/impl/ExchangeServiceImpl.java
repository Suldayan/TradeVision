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

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {
    private final ExchangeClient exchangeClient;
    private final ExchangeMapper exchangeMapper;

    @Nonnull
    @Override
    public Set<Exchange> getExchangeData() {
        try {
            ExchangeWrapper exchangeHolder = exchangeClient.getExchanges();
            log.info("Fetched exchanges with result: {}", exchangeHolder);
            if (exchangeHolder == null) {
                log.error("Fetched data from exchange wrapper returned as null");
                throw new ApiException("Exchanges data fetched but return as null");
            }
            return exchangeHolder.exchanges();
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
