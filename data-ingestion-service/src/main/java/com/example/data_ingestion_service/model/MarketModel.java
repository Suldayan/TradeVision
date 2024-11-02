package com.example.data_ingestion_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
public class MarketModel {
    @JsonProperty("exchangeId")
    @NotBlank(message = "Exchange ID cannot be blank")
    private String exchangeId;

    @JsonProperty("rank")
    @NotBlank(message = "Rank cannot be blank")
    private String rank;

    @JsonProperty("baseSymbol")
    @NotBlank(message = "Base Symbol cannot be blank")
    private String baseSymbol;

    @JsonProperty("baseId")
    @NotBlank(message = "Base ID cannot be blank")
    private String baseId;

    @JsonProperty("quoteSymbol")
    @NotBlank(message = "Quote Symbol cannot be blank")
    private String quoteSymbol;

    @JsonProperty("quoteId")
    @NotBlank(message = "Quote ID cannot be blank")
    private String quoteId;

    @JsonProperty("priceQuote")
    @NotBlank(message = "Price Quote cannot be blank")
    private String priceQuote;

    @JsonProperty("priceUsd")
    @NotBlank(message = "Price cannot be blank")
    private String priceUsd;

    @JsonProperty("volumeUsd24Hr")
    @NotBlank(message = "Volume cannot be blank")
    private String volumeUsd24Hr;

    @JsonProperty("percentExchangeVolume")
    @NotBlank(message = "Percentage Exchange Volume cannot be blank")
    private String percentExchangeVolume;

    @JsonProperty("tradesCount24Hr")
    @NotBlank(message = "24h Trades Count cannot be blank")
    private String tradesCount24Hr;

    @JsonProperty("updated")
    @NotNull(message = "Updated timestamp cannot be null")
    private long updated;
}
