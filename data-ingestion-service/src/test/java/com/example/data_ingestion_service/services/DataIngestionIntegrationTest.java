package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.services.impl.DataAggregateServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class DataIngestionIntegrationTest {
    @Autowired
    private DataAggregateServiceImpl dataAggregateService;

    @Test
    void testFetchDataAsyncWithRealApi() {
        assertDoesNotThrow(dataAggregateService::fetchDataAsync);
    }
}
