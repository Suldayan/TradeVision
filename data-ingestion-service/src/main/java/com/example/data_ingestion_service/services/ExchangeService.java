package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.models.RawExchangesModel;
import com.example.data_ingestion_service.records.Exchange;

import java.util.Set;

public interface ExchangeService {
    Set<Exchange> getExchangeData();
    Set<RawExchangesModel> convertToModel();
}
