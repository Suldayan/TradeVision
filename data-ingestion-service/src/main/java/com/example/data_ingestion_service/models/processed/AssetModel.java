package com.example.data_ingestion_service.models.processed;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "asset")
public class AssetModel {
    // The asset id is the name of the given asset
    @Id
    private String id;

    private BigDecimal priceUsd;
    private LocalDateTime fetched;
}
