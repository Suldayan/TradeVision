package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.models.raw.RawAssetModel;
import com.example.data_processing_service.models.raw.RawExchangesModel;
import com.example.data_processing_service.models.raw.RawMarketModel;
import com.example.data_processing_service.services.ComposeAsyncService;
import com.example.data_processing_service.services.FilterService;
import com.example.data_processing_service.services.TransformationService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Slf4j
@Service
public class ComposeAsyncServiceImpl implements ComposeAsyncService {
    private final FilterService filterService;
    private final TransformationService transformationService;


    @KafkaListener(topics = "status", groupId = "configure this properly")
    @Override
    public void consumeStatusIndicator(String status) {
        log.info("Consumed status from status topic at: {}", LocalTime.now());
    }

    @Override
    public CompletableFuture<Void> orchestrateFilterFlowAsync(@Nonnull Long timestamp) {
        CompletableFuture<Set<RawMarketModel>> marketFutures = CompletableFuture.supplyAsync(filterService::filterMarkets)
                .exceptionally(ex -> {
                    log.error("");
                    return null;
                });
        CompletableFuture<Set<String>> exchangeIdFutures = marketFutures.thenApply(filterService::filterExchangeIds);
        CompletableFuture<Set<String>> assetIdFutures = marketFutures.thenApply(filterService::filterAssetIds);
        CompletableFuture<Set<RawExchangesModel>> rawExchangeModelFutures = exchangeIdFutures.thenApply(filterService::exchangeIdsToModels);
        CompletableFuture<Set<RawAssetModel>> rawAssetModelFutures = assetIdFutures.thenApply(filterService::assetIdsToModels);

        return CompletableFuture.allOf(rawExchangeModelFutures, rawAssetModelFutures)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Exchanges and Assets successfully converted from ids");
                    }
                }).exceptionally(ex -> {
                    log.error("");
                    return null;
                });
    }

    @Override
    public CompletableFuture<Void> orchestrateTransformationFlowAsync() {
        return null;
    }

    private void handleError(String msg, Throwable ex) {
        throw new RuntimeException();
    }
}
