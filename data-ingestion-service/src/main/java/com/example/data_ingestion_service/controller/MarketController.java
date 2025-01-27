package com.example.data_ingestion_service.controller;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.repository.RawMarketModelRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/data-ingestion")
@Slf4j
@RequiredArgsConstructor
public class MarketController {
    private final RawMarketModelRepository marketModelRepository;

    @Nonnull
    @GetMapping(
            value = "/markets/{timestamp}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Set<RawMarketModel>> fetchMarkets(@Nonnull @PathVariable Long timestamp) {
        log.info("Fetching data for batch time: {}, at {}", timestamp, LocalDateTime.now());
        Set<RawMarketModel> marketModels = marketModelRepository.findAllByTimestamp(timestamp);
        if (marketModels.isEmpty()) {
            log.warn("No market models found for timestamp: {}", timestamp);
            return ResponseEntity.ok(Collections.emptySet());
        }
        if (marketModels.size() != 100) {
            log.warn("Market set size is {} (expected 100) for timestamp: {}", marketModels.size(), timestamp);
        }
        return ResponseEntity.ok(marketModels);
    }
}
