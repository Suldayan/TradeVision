package com.example.data_ingestion_service.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "raw_markets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawMarketModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String modelId;

    @JsonProperty("baseId")
    private String baseId;

    @JsonProperty("rank")
    private Integer rank;

    @JsonProperty("priceQuote")
    private BigDecimal priceQuote;

    @JsonProperty("priceUsd")
    private BigDecimal priceUsd;

    @JsonProperty("volumeUsd24Hr")
    private BigDecimal volumeUsd24Hr;

    @JsonProperty("percentExchangeVolume")
    private BigDecimal percentExchangeVolume;

    @JsonProperty("tradesCount24Hr")
    @Nullable
    private Integer tradesCount;

    @JsonProperty("updated")
    private Long updated;

    @JsonProperty("exchangeId")
    private String exchangeId;

    @JsonProperty("quoteId")
    private String quoteId;

    @JsonProperty("baseSymbol")
    private String baseSymbol;

    @JsonProperty("quoteSymbol")
    private String quoteSymbol;
}