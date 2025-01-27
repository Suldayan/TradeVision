package com.example.data_ingestion_service.controller;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.repository.RawMarketModelRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/data-ingestion")
@Slf4j
@RequiredArgsConstructor
public class MarketController {
    private final RawMarketModelRepository marketModelRepository;

    @Nonnull
    @GetMapping("/markets/{timestamp}")
    public ResponseEntity<Set<RawMarketModel>> fetchMarkets(@Nonnull @RequestParam Long timestamp) {
        try {
            return ResponseEntity.of(Optional.ofNullable(marketModelRepository.findAllByTimestamp(timestamp)));
        } catch (Exception e) {
            log.error("Failed to fetch market data at: {} with timestamp: {}. {}",
                    LocalDateTime.now(), timestamp, e.getMessage());
            return ResponseEntity.ok(Collections.emptySet());
        }
    }
}
