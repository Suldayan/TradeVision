package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.models.raw.RawExchangesModel;

import java.util.List;

public interface ExchangeService {
    List<RawExchangesModel> getExchangeData();
}
