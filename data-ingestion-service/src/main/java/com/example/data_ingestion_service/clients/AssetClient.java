package com.example.data_ingestion_service.clients;

import com.example.data_ingestion_service.records.wrapper.AssetWrapper;
import org.springframework.web.service.annotation.GetExchange;

public interface AssetClient {

    @GetExchange("/assets")
    AssetWrapper getAssets();
}
