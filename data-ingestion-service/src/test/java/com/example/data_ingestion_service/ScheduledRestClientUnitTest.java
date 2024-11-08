package com.example.data_ingestion_service;

import com.example.data_ingestion_service.client.ScheduledRestClient;
import com.example.data_ingestion_service.model.MarketModel;
import com.example.data_ingestion_service.service.DataFilterService;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ScheduledRestClientUnitTest<T> {

    @Mock
    private RestClient restClient;
    @Mock
    private Bucket bucket;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private Map<String, Class<T>> dataTypeToModel;
    @Mock
    private MarketModel marketModel;
    @Mock
    private DataFilterService<T> filterService;

    @InjectMocks
    private ScheduledRestClient<T> scheduledRestClient;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testManageFetch_Success() {
        // Define test URL and expected result
        String testUrl = "https://test";
        dataTypeToModel = Map.of(testUrl, (Class<T>) MarketModel.class);

        // Mock fetchData to return an expected result
        T expectedResult = (T) mock(MarketModel.class);
        when(scheduledRestClient.fetchData(testUrl, (Class<T>) MarketModel.class))
                .thenReturn(expectedResult);

        // Create a CompletableFuture that will complete with the expected result
        CompletableFuture<Void> completableFutureMock = CompletableFuture.completedFuture(null);

        // Mock filterService to return a completed CompletableFuture
        doNothing().when(filterService).filter(expectedResult);

        // Execute the method under test
        scheduledRestClient.manageFetch();

        // Verify fetchData and filter were called as expected
        verify(scheduledRestClient).fetchData(testUrl, (Class<T>) MarketModel.class);
        verify(filterService).filter(expectedResult);
    }



}
