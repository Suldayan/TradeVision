package com.example.trade_vision_backend.ingestion.internal;

import com.example.trade_vision_backend.ingestion.internal.infrastructure.repository.IngestionRepository;
import com.example.trade_vision_backend.ingestion.internal.infrastructure.service.IngestionService;
import com.example.trade_vision_backend.ingestion.market.RawMarketModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class IngestionServiceIntegrationTest {

    @Autowired
    private IngestionRepository repository;

    @Autowired
    private IngestionService ingestionService;

    @Test
    public void executeIngestion_SuccessfullyExecutesEntireFlow() {
        assertDoesNotThrow(() -> ingestionService.executeIngestion());
    }

    @Test
    public void executeIngestion_SuccessfullyExecutesEntireFlowAndUpdatesDataOnExistingData() throws InterruptedException {
        assertDoesNotThrow(() -> ingestionService.executeIngestion());
        List<RawMarketModel> oldData = repository.findAll();

        Thread.sleep(5000);

        assertDoesNotThrow(() -> ingestionService.executeIngestion());
        List<RawMarketModel> updatedData = repository.findAll();

        assertEquals(100, oldData.size());
        assertEquals(100, updatedData.size());
        assertNotEquals(oldData, updatedData);
    }
}
