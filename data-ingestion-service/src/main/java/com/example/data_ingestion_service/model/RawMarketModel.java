package com.example.data_ingestion_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "raw_market_data")
@Data
@Builder
public class RawMarketModel {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JsonProperty("exchangeId")
    private String exchangeId;

    @JsonProperty("rank")
    private int rank;

    @JsonProperty("baseId")
    private String baseId;

    @JsonProperty("quoteId")
    private String quoteId;

    @JsonProperty("baseSymbol")
    private String baseSymbol;

    @JsonProperty("quoteSymbol")
    private String quoteSymbol;

    @JsonProperty("priceQuote")
    private BigDecimal priceQuote;

    @JsonProperty("priceUsd")
    private BigDecimal priceUsd;

    @JsonProperty("volumeUsd24Hr")
    private BigDecimal volumeUsd24Hr;

    @JsonProperty("percentExchangeVolume")
    private BigDecimal percentExchangeVolume;

    @JsonProperty("tradesCount24Hr")
    private Long tradesCount24Hr;

    @JsonProperty("updated")
    private Long updated;
}
