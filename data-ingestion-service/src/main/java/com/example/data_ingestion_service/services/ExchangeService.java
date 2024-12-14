package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.models.RawExchangeWrapperModel;
import com.example.data_ingestion_service.models.RawExchangesModel;

import java.util.List;

public interface ExchangeService {
    RawExchangeWrapperModel getExchangeData();
    List<RawExchangesModel> getExchangeDataAsList();
}
