package com.example.data_ingestion_service.client;

import com.example.data_ingestion_service.model.RawMarketWrapperModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.service.annotation.GetExchange;

public interface ApiClient {

    @GetExchange("/market")
    ResponseEntity<RawMarketWrapperModel> getMarketData();

    //TODO: configure the rest of the wrapper models
}
