package com.example.trade_vision_backend.ingestion;

import com.example.trade_vision_backend.ingestion.internal.application.IngestionService;
import com.example.trade_vision_backend.ingestion.internal.infrastructure.repository.IngestionRepository;
import com.example.trade_vision_backend.ingestion.market.MarketService;
import com.example.trade_vision_backend.ingestion.market.RawMarketDTO;
import com.example.trade_vision_backend.ingestion.market.RawMarketModel;
import com.example.trade_vision_backend.ingestion.market.domain.dto.MarketWrapperDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ApplicationModuleTest(ApplicationModuleTest.BootstrapMode.DIRECT_DEPENDENCIES)
@ActiveProfiles("test")
public class IngestionIntegrationTests {

    @MockitoBean
    private MarketService marketService;

    @MockitoBean
    private IngestionRepository ingestionRepository;

    @MockitoBean
    private IngestionManagement ingestionManagement;

    @Autowired
    private IngestionService ingestionService;

    @Test
    public void eventReceivedThroughPublishing(Scenario scenario) {
        UUID testID = UUID.randomUUID();

        scenario.publish(
                new IngestionCompleted(
                        testID,
                        100,
                        Instant.now(),
                        123456789L,
                        this
                )
        ).andWaitForEventOfType(IngestionCompleted.class)
                .matching(event -> event.id().equals(testID) && event.marketCount() == 100)
                .toArriveAndVerify(event -> {
                    assertNotNull(event);
                    assertEquals(testID, event.id());
                    assertEquals(123456789L, event.ingestedTimestamp());
                    assertEquals(100, event.marketCount());
                });
    }

    @Test
    public void eventSentWhenIngestionCompletes(Scenario scenario) {
        Long testTimestamp = 123456789L;
        UUID testID = UUID.randomUUID();
        Set<RawMarketDTO> rawMarketDTOS = createValidMarketDTOSet();
        MarketWrapperDTO marketWrapper = new MarketWrapperDTO(rawMarketDTOS, testTimestamp);
        List<RawMarketModel> rawMarketModels = createValidMarketModelList();

        when(marketService.getMarketsData()).thenReturn(marketWrapper);
        when(marketService.convertWrapperDataToRecord(marketWrapper)).thenReturn(rawMarketDTOS);
        when(marketService.rawMarketDTOToModel(rawMarketDTOS)).thenReturn(rawMarketModels);
        when(ingestionRepository.findAll()).thenReturn(new ArrayList<>());
        doNothing().when(ingestionManagement).complete(rawMarketDTOS);

        scenario.stimulate(() -> ingestionService.executeIngestion())
                .andWaitForEventOfType(IngestionCompleted.class)
                .matching(event -> event.ingestedTimestamp().equals(testTimestamp))
                .toArriveAndVerify(event -> {
                    assertNotNull(event);
                    verify(ingestionManagement).complete(rawMarketDTOS);
                    verify(ingestionRepository).saveAll(any());
                });
    }

    private static Set<RawMarketDTO> createValidMarketDTOSet() {
        Set<RawMarketDTO> set = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            set.add(new RawMarketDTO(
                    "binance",
                    i + 1,
                    "BTC",
                    "bitcoin",
                    "USDT",
                    "tether",
                    new BigDecimal("65000.00").add(new BigDecimal(i)),
                    new BigDecimal("65000.00"),
                    new BigDecimal("1500000000.00"),
                    new BigDecimal("5.25"),
                    1200 + i,
                    1696252800000L + i,
                    null
            ));
        }

        return set;
    }

    private static List<RawMarketModel> createValidMarketModelList() {
        List<RawMarketModel> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(new RawMarketModel(
                    UUID.randomUUID(),
                    "binance",
                    i + 1,
                    "BTC",
                    "bitcoin",
                    "USDT",
                    "tether",
                    new BigDecimal("65000.00").add(new BigDecimal(i)),
                    new BigDecimal("65000.00"),
                    new BigDecimal("1500000000.00"),
                    new BigDecimal("5.25"),
                    1200 + i,
                    1696252800000L + i,
                    null
            ));
        }

        return list;
    }
}
