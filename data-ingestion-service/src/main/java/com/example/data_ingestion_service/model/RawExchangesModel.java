package com.example.data_ingestion_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "raw_exchanges_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawExchangesModel {
    @Id
    @JsonProperty("id")
    private String id;

    @Column(unique = true)
    @JsonProperty("name")
    private String name;

    @JsonProperty("percentTotalVolume")
    private BigDecimal percentTotalVolume;

    @JsonProperty("volumeUsd")
    private BigDecimal volumeUsd;

    @JsonProperty("updated")
    private Long updated;

    @OneToMany(mappedBy = "exchange")
    private Set<RawMarketModel> markets;
}