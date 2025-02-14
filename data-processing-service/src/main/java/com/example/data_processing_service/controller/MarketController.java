package com.example.data_processing_service.controller;

import com.example.data_processing_service.database.model.MarketModel;
import com.example.data_processing_service.database.repository.MarketModelRepository;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(
        value = "api/v1/processing",
        produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
public class MarketController {
    private final MarketModelRepository marketModelRepository;

    @GetMapping("/all")
    ResponseEntity<Set<MarketModel>> fetchAllMarketModelsByTimeRange(
            @RequestParam @Valid @Nonnull Long startDate,
            @RequestParam @Valid @Nonnull Long endDate) throws IllegalArgumentException {

        validateTimestamps(startDate, endDate);
        ZonedDateTime start = convertLongToZonedDateTime(startDate);
        ZonedDateTime end = convertLongToZonedDateTime(endDate);

        log.debug("Fetching market models between {} and {}", start, end);
        Set<MarketModel> marketModels = marketModelRepository.findAllByTimestampBetween(start, end);
        if (isEmpty(marketModels)) {
            return ResponseEntity.ok(Collections.emptySet());
        }
        Set<MarketModel> sortedMarketModels = sortMarketModelsByTimestamp(marketModels);

        return ResponseEntity.ok(sortedMarketModels);
    }

    @GetMapping("/base/{id}")
    ResponseEntity<Set<MarketModel>> fetchModelByBaseIdAndTimeRange(
            @RequestParam @Valid @Nonnull Long startDate,
            @RequestParam @Valid @Nonnull Long endDate,
            @PathVariable @Valid @Nonnull String id) throws IllegalArgumentException {

        validateTimestamps(startDate, endDate);
        ZonedDateTime start = convertLongToZonedDateTime(startDate);
        ZonedDateTime end = convertLongToZonedDateTime(endDate);

        log.debug("Fetching base market models between {} and {} with id: {}",
                start, end, id);
        Set<MarketModel> marketModels = marketModelRepository.findByBaseIdAndTimestampBetween(id, start, end);
        if (isEmpty(marketModels)) {
            return ResponseEntity.ok(Collections.emptySet());
        }
        Set<MarketModel> sortedMarketModels = sortMarketModelsByTimestamp(marketModels);

        return ResponseEntity.ok(sortedMarketModels);
    }

    @GetMapping("/quote/{id}")
    ResponseEntity<Set<MarketModel>> fetchModelByQuoteIdAndTimeRange(
            @RequestParam @Valid @Nonnull Long startDate,
            @RequestParam @Valid @Nonnull Long endDate,
            @PathVariable @Valid @Nonnull String id) throws IllegalArgumentException {

        validateTimestamps(startDate, endDate);
        ZonedDateTime start = convertLongToZonedDateTime(startDate);
        ZonedDateTime end = convertLongToZonedDateTime(endDate);

        log.debug("Fetching quote market models between {} and {} with id: {}",
                start, end, id);
        Set<MarketModel> marketModels = marketModelRepository.findByQuoteIdAndTimestampBetween(id, start, end);
        if (isEmpty(marketModels)) {
            return ResponseEntity.ok(Collections.emptySet());
        }
        Set<MarketModel> sortedMarketModels = sortMarketModelsByTimestamp(marketModels);

        return ResponseEntity.ok(sortedMarketModels);
    }

    @GetMapping("/exchange/{id}")
    ResponseEntity<Set<MarketModel>> fetchModelByExchangeIdAndTimeRange(
            @RequestParam @Valid @Nonnull Long startDate,
            @RequestParam @Valid @Nonnull Long endDate,
            @RequestParam @Valid @Nonnull String id) throws IllegalArgumentException {

        validateTimestamps(startDate, endDate);
        ZonedDateTime start = convertLongToZonedDateTime(startDate);
        ZonedDateTime end = convertLongToZonedDateTime(endDate);

        log.debug("Fetching exchange market models between {} and {} with id: {}",
                start, end, id);
        Set<MarketModel> marketModels = marketModelRepository.findByExchangeIdAndTimestampBetween(id, start, end);
        if (isEmpty(marketModels)) {
            return ResponseEntity.ok(Collections.emptySet());
        }
        Set<MarketModel> sortedMarketModels = sortMarketModelsByTimestamp(marketModels);

        return ResponseEntity.ok(sortedMarketModels);
    }

    private boolean isEmpty(@Nonnull Set<MarketModel> marketModels) {
        return marketModels.isEmpty();
    }

    private void validateTimestamps(@Nonnull Long start, @Nonnull Long end) throws IllegalArgumentException {
        if (start >= end) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }

    @Nonnull
    private Set<MarketModel> sortMarketModelsByTimestamp(@Nonnull Set<MarketModel> marketModels) {
        return marketModels.stream()
                .sorted(Comparator.comparing(MarketModel::getTimestamp))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Nonnull
    private ZonedDateTime convertLongToZonedDateTime(@Nonnull Long date) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneOffset.UTC);
    }
}