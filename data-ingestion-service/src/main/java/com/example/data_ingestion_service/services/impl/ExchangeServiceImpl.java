package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.clients.ExchangeClient;
import com.example.data_ingestion_service.models.RawExchangeWrapperModel;
import com.example.data_ingestion_service.models.RawExchangesModel;
import com.example.data_ingestion_service.services.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {
    private final ExchangeClient exchangeClient;

    @Override
    public RawExchangeWrapperModel getExchangeData() {
        return exchangeClient.getExchanges();
    }

    @Override
    public List<RawExchangesModel> getExchangeDataAsList() {
        return getExchangeData().getRawExchangesModelList();
    }
}
