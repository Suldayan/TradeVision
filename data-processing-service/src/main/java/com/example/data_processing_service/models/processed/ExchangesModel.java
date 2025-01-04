package com.example.data_processing_service.models.processed;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "exchanges")
public class ExchangesModel {
    @Id
    String id;

    private BigDecimal percentTotalVolume;
    private BigDecimal volumeUsd;
    private Long updated;
}
