package com.example.data_processing_service.services;

import java.util.concurrent.CompletableFuture;

public interface ComposeAsyncService {
    CompletableFuture<Void> orchestrateFlowAsync();
}
