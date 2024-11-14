package com.example.data_ingestion_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "raw_asset_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawAssetHistoryModel {
    @Id
    private String id;

    @JsonProperty("priceUsd")
    private BigDecimal priceUsd;

    @JsonProperty("time")
    private Long time;

    @OneToMany(mappedBy = "base_id")
    private List<RawMarketModel> markets;
}