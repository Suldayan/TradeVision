package com.example.data_ingestion_service.records.wrapper;

import com.example.data_ingestion_service.records.Asset;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;

import java.util.Set;

public record AssetWrapper(@JsonProperty(value = "data") @Nonnull Set<Asset> assets,
                           @JsonProperty(value = "timestamp") @Nonnull Long timestamp) {
}
