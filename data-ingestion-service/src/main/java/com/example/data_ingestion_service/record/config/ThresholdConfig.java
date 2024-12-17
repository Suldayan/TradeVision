package com.example.data_ingestion_service.record.config;

import com.example.data_ingestion_service.record.DataThreshold;

import java.math.BigDecimal;
import java.util.Map;

public class ThresholdConfig {
    public static final Map<String, DataThreshold> marketThreshold = Map.of(
            "priceUsd", new DataThreshold("priceUsd", new BigDecimal("0.1"))
            // add more
    );

    public static final Map<String, DataThreshold> exchangeThreshold = Map.of();

    public static final Map<String, DataThreshold> assetThreshold = Map.of();
}

