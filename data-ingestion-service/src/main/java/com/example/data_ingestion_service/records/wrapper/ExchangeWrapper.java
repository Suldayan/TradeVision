package com.example.data_ingestion_service.records.wrapper;

import com.example.data_ingestion_service.records.Exchange;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;

import java.util.Set;

public record ExchangeWrapper(@JsonProperty(value = "data") @Nonnull Set<Exchange> exchanges,
                              @JsonProperty(value = "timestamp") @Nonnull Long timestamp) {
}
