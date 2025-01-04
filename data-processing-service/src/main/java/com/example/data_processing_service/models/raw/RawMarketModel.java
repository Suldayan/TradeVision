package com.example.data_processing_service.models.raw;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawMarketModel {
    @Id
    @JsonProperty("baseId")
    private String id;

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
