package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.models.raw.RawMarketModel;
import com.example.data_processing_service.services.ComposeAsyncService;
import com.example.data_processing_service.services.FilterService;
import com.example.data_processing_service.services.TransformationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Slf4j
@Service
public class ComposeAsyncServiceImpl implements ComposeAsyncService {
    private final FilterService filterService;
    private final TransformationService transformationService;

    @Override
    public CompletableFuture<Void> orchestrateFlowAsync() {
        CompletableFuture<Set<RawMarketModel>> marketFutures = CompletableFuture.supplyAsync(filterService::collectAndUpdateMarketState)



    }
}
