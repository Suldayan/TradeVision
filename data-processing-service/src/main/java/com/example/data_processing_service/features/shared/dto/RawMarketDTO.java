package com.example.data_processing_service.features.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawMarketDTO {
    @JsonProperty("baseId")
    private String baseId;

    @JsonProperty("rank")
    @JsonIgnore
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
    private Integer tradesCount24Hr;

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

    private Long timestamp;
}
