package com.example.data_processing_service.controller;

import com.example.data_processing_service.models.MarketModel;
import com.example.data_processing_service.repository.MarketModelRepository;
import com.example.data_processing_service.services.exception.DataValidationException;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Set;

@RestController
@RequestMapping(value = "api/v1/processing")
@Slf4j
@RequiredArgsConstructor
public class MarketController {
    private final MarketModelRepository marketModelRepository;

    @GetMapping("/markets")
    ResponseEntity<Set<MarketModel>> fetchMarketModelsByFilteredTimestamps(
            @RequestParam @Nonnull Long startDate,
            @RequestParam @Nonnull Long endDate) throws DataValidationException {

        validateTimestamps(startDate, endDate);
        ZonedDateTime start = convertLongToZonedDateTime(startDate);
        ZonedDateTime end = convertLongToZonedDateTime(endDate);

        log.debug("Fetching market models between {} and {}", start, end);
        Set<MarketModel> marketModels = marketModelRepository.findAllByTimestampBetween(start, end);

        if (isEmpty(marketModels)) {
            return ResponseEntity.ok(Collections.emptySet());
        }
        return ResponseEntity.ok(marketModels);
    }

    private boolean isEmpty(@Nonnull Set<MarketModel> marketModels) {
        return marketModels.isEmpty();
    }

    private void validateTimestamps(@Nonnull Long start, @Nonnull Long end) throws DataValidationException {
        if (start >= end) {
            throw new DataValidationException("Start date must be before end date");
        }
    }

    @Nonnull
    private ZonedDateTime convertLongToZonedDateTime(@Nonnull Long date) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneOffset.UTC);
    }
}