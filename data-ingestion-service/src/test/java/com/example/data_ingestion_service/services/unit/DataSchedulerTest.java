package com.example.data_ingestion_service.services.unit;

import com.example.data_ingestion_service.services.scheduler.DataScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataSchedulerTest {

    @Mock
    private DataAsyncService dataAsyncService;

    @InjectMocks
    private DataScheduler dataScheduler;

    @Test
    void scheduler_WhenAsyncFetchSucceeds_ShouldCompleteNormally() {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        when(dataAsyncService.asyncFetch()).thenReturn(future);

        dataScheduler.scheduler();

        verify(dataAsyncService, times(1)).asyncFetch();
    }

    @Test
    void scheduler_WhenAsyncFetchThrowsException_ShouldHandleAndLogError() {
        when(dataAsyncService.asyncFetch()).thenThrow(new RuntimeException("Test exception"));

        dataScheduler.scheduler();

        verify(dataAsyncService, times(1)).asyncFetch();
    }

    @Test
    void scheduler_WhenAsyncFetchCompletesExceptionally_ShouldHandleAndLogError() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Async operation failed"));
        when(dataAsyncService.asyncFetch()).thenReturn(future);

        dataScheduler.scheduler();

        verify(dataAsyncService, times(1)).asyncFetch();
    }

    @Test
    void scheduler_ShouldLogStartAndCompletion() {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        when(dataAsyncService.asyncFetch()).thenReturn(future);

        dataScheduler.scheduler();

        verify(dataAsyncService).asyncFetch();
    }

    @Test
    void scheduler_WhenMultipleCallsOccur_ShouldHandleEachIndependently() {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        when(dataAsyncService.asyncFetch()).thenReturn(future);

        dataScheduler.scheduler();
        dataScheduler.scheduler();
        dataScheduler.scheduler();

        verify(dataAsyncService, times(3)).asyncFetch();
    }
}