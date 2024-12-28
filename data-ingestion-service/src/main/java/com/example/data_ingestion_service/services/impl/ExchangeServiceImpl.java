package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.clients.ExchangeClient;
import com.example.data_ingestion_service.models.RawExchangeWrapperModel;
import com.example.data_ingestion_service.models.raw.RawExchangesModel;
import com.example.data_ingestion_service.services.ExchangeService;
import com.example.data_ingestion_service.services.exceptions.ApiException;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {
    private final ExchangeClient exchangeClient;

    /*
     * Grabs all the exchange data from the exchanges endpoint
     * @returns a list of each exchange, containing exchange metadata
     * */
    @Nonnull
    @Override
    public List<RawExchangesModel>  getExchangeData() {
        try {
            RawExchangeWrapperModel exchangeHolder = exchangeClient.getExchanges();
            log.info("Fetched exchanges with result: {}", exchangeHolder);
            if (exchangeHolder == null) {
                log.error("Fetched data from exchange wrapper returned as null");
                throw new ApiException("Exchanges data fetched but return as null");
            }
            return exchangeHolder.getRawExchangesModelList();
        } catch (Exception e) {
            log.error("An error occurred while fetching exchanges data: {}", e.getMessage());
            throw new ApiException(String.format("Failed to fetch exchange wrapper data: %s", e));
        }
    }

}
