package com.example.data_ingestion_service.records.wrapper;

import com.example.data_ingestion_service.records.Market;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import lombok.Builder;

import java.util.Set;

@Builder
public record MarketWrapper(@JsonProperty(value = "data") @Nonnull Set<Market> markets,
                            @JsonProperty(value = "timestamp") @Nonnull Long timestamp) {
}
