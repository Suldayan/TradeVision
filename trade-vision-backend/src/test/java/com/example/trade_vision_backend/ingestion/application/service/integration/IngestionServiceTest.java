package com.example.trade_vision_backend.ingestion.application.service.integration;

import com.example.trade_vision_backend.ingestion.application.management.IngestionManagement;
import com.example.trade_vision_backend.ingestion.domain.IngestionCompleted;
import com.example.trade_vision_backend.ingestion.domain.service.IngestionService;
import com.example.trade_vision_backend.ingestion.infrastructure.client.IngestionClient;
import com.example.trade_vision_backend.ingestion.domain.RawMarketDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ApplicationModuleTest(ApplicationModuleTest.BootstrapMode.DIRECT_DEPENDENCIES)
public class IngestionServiceTest {

    @Autowired
    private Scenario scenario;

    @MockitoBean
    private IngestionClient ingestionClient;

    @MockitoBean
    private IngestionManagement ingestionManagement;

    @Autowired
    private IngestionService ingestionService;

    @Test
    void executeIngestion_SuccessfullyExecutesFullIngestionFlow() {
        Set<RawMarketDTO> marketDTOS = createValidMarketDTOs();

        assertDoesNotThrow(() -> ingestionService.executeIngestion());

        scenario.publish(new IngestionCompleted(this, marketDTOS))
                .andWaitForEventOfType(IngestionCompleted.class)
                .matching(event -> event.getRawMarketDTOS().equals(marketDTOS))
                .toArriveAndVerify(event -> {
                    assertNotNull(event);
                    assertEquals(marketDTOS, event.getRawMarketDTOS());
        });
    }

    private static Set<RawMarketDTO> createValidMarketDTOs() {
        Set<RawMarketDTO> set = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            set.add(new RawMarketDTO(
                    "binance",
                    i + 1,
                    "BTC",
                    "bitcoin",
                    "USDT",
                    "tether",
                    new BigDecimal("65000.00"),
                    new BigDecimal("65000.00"),
                    new BigDecimal("1500000000.00"),
                    new BigDecimal("5.25"),
                    1200,
                    1696252800000L,
                    null
            ));
        }

        return set;
    }
}
