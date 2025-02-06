package com.example.data_processing_service.controller;

import com.example.data_processing_service.models.MarketModel;
import com.example.data_processing_service.repository.MarketModelRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Set;

@RestController
@RequestMapping(value = "api/v1/processing")
@Slf4j
@RequiredArgsConstructor
public class MarketController {
    private final MarketModelRepository marketModelRepository;

    @GetMapping("/markets/{start}/{end}")
    ResponseEntity<Set<MarketModel>> fetchMarketModelsByFilteredTimestamps(
            @PathVariable @Nonnull Long dateOne,
            @PathVariable @Nonnull Long dateTwo) {
        ZonedDateTime start = convertLongToZonedDateTime(dateOne);
        ZonedDateTime end = convertLongToZonedDateTime(dateTwo);

        

    }

    @Nonnull
    private ZonedDateTime convertLongToZonedDateTime(@Nonnull Long date) {
        return ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(date),
                ZoneOffset.UTC
        );
    }
}
