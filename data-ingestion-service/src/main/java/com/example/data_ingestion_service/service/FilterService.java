package com.example.data_ingestion_service.service;

import com.example.data_ingestion_service.model.RawAssetWrapperModel;
import com.example.data_ingestion_service.model.RawExchangeWrapperModel;
import com.example.data_ingestion_service.model.RawMarketWrapperModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FilterService {

    // TODO: process the data by comparison between previous models so only meaningful data is pushed
    public void processMarketData(RawMarketWrapperModel marketData) {

    }

    public void processExchangeData(RawExchangeWrapperModel exchangeData) {

    }

    public void processAssetHistoryData(RawAssetWrapperModel assetHistoryData) {

    }
}
