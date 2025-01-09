package com.example.data_ingestion_service.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "raw_exchanges")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawExchangesModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String modelId;

    @JsonProperty("exchangeId")
    private String exchangeId;

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
}