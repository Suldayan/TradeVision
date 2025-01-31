package com.example.data_processing_service.client;

import com.example.data_processing_service.models.RawMarketModel;
import org.springframework.web.service.annotation.GetExchange;

import java.util.Set;

public interface IngestionClient {
    @GetExchange(url = "/markets/{timestamp}")
    Set<RawMarketModel> getRawMarketModels(Long timestamp);
}
