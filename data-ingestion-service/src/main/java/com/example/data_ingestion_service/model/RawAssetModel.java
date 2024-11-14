package com.example.data_ingestion_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "raw_asset")
public class RawAssetModel {
    @JsonProperty("id")
    @Id
    private String id;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("name")
    private String name;

    @JsonProperty("supply")
    private BigDecimal supply;

    @JsonProperty("maxSupply")
    private BigDecimal maxSupply;

    @JsonProperty("marketCapUsd")
    private BigDecimal marketCapUsd;

    @JsonProperty("volumeUsd24Hr")
    private BigDecimal volumeUsd24Hr;

    @JsonProperty("priceUsd")
    private BigDecimal priceUsd;

    @JsonProperty("changePercent24Hr")
    private BigDecimal changePercent24Hr;

    @JsonProperty("vwap24Hr")
    private BigDecimal vwap24Hr;
}
