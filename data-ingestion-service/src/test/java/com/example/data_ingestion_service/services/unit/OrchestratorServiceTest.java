package com.example.data_ingestion_service.services.unit;

import com.example.data_ingestion_service.services.MarketService;
import com.example.data_ingestion_service.services.impl.OrchestratorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrchestratorServiceTest {

    @Mock
    MarketService marketService;

    @InjectMocks
    OrchestratorServiceImpl orchestratorService;

    @Test
    void executeDataPipeline_SuccessfullyCompletesFlow() {

    }
}
