package com.example.trade_vision_backend.ingestion;

import java.time.Instant;
import java.util.UUID;

public record IngestionCompleted(
        UUID id,
        int marketCount,
        Instant completedAt,
        Long ingestedTimestamp,
        String source
) {
    public IngestionCompleted(
            UUID ingestionId,
            int marketCount,
            Long ingestedTimestamp,
            Object source) {
        this(
                ingestionId,
                marketCount,
                Instant.now(),
                ingestedTimestamp,
                source.getClass().getSimpleName()
        );
    }
}
