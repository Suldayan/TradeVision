package com.example.data_processing_service.models.raw;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
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
public class RawExchangesModel {
    @Id
    @JsonProperty("exchangeId")
    private String id;

    @Column(unique = true)
    @JsonProperty("name")
    private String name;

    @JsonProperty("rank")
    @JsonIgnore
    private Integer rank;

    @JsonProperty("percentTotalVolume")
    private BigDecimal percentTotalVolume;

    @JsonProperty("volumeUsd")
    private BigDecimal volumeUsd;

    @JsonProperty("tradingPairs")
    private Integer tradingPairs;

    @JsonProperty("socket")
    @JsonIgnore
    private boolean socket;

    @JsonProperty("exchangeUrl")
    @JsonIgnore
    private String exchangeUrl;

    @JsonProperty("updated")
    private Long updated;

    private Long timestamp;
}
