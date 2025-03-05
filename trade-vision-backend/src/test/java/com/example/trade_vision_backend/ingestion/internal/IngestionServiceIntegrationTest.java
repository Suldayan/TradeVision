package com.example.trade_vision_backend.ingestion.internal;

import com.example.trade_vision_backend.ingestion.internal.infrastructure.service.IngestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class IngestionServiceIntegrationTest {

    @Autowired
    private IngestionService ingestionService;

    @Test
    public void executeIngestion_SuccessfullyExecutesEntireFlow() {
        assertDoesNotThrow(() -> ingestionService.executeIngestion());
    }
}
