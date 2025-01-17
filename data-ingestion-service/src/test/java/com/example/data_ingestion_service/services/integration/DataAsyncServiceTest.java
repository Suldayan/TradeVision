package com.example.data_ingestion_service.services.integration;

import com.example.data_ingestion_service.services.DataAsyncService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class DataAsyncServiceTest {

    @Autowired
    DataAsyncService dataAsyncService;

    @Test
    void asyncFetch_Completes_AllAsyncTasks() {
        CompletableFuture<Void> completedAsyncTasks = dataAsyncService.asyncFetch();

        assertFalse(completedAsyncTasks.isCompletedExceptionally());
    }
}
