package com.example.data_processing_service.services;

import java.util.concurrent.CompletableFuture;

public interface ComposeAsyncService {
    void consumeStatusIndicator(String status);
    CompletableFuture<Void> orchestrateFilterFlowAsync(Long timestamp);
    CompletableFuture<Void> orchestrateTransformationFlowAsync();
}
