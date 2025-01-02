package com.example.data_ingestion_service.models.processed;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "exchanges")
public class ExchangeModel {
    @Id
    String id;

    private BigDecimal percentTotalVolume;
    private BigDecimal volumeUsd;
    private Long updated;
}
