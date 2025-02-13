package com.example.data_processing_service.features.ingestion.client;

import com.example.data_processing_service.features.shared.dto.RawMarketDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import java.util.Set;

public interface IngestionClient {
    @GetExchange(url = "/markets/{timestamp}")
    Set<RawMarketDTO> getRawMarketModels(@PathVariable Long timestamp);
}
