package com.example.data_processing_service.models.processed;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "exchanges")
public class ExchangesModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String exchangeId;

    private BigDecimal percentTotalVolume;
    private BigDecimal volumeUsd;
    private Long updated;

    private Long timestamp;

    private Instant createdAt;
}
