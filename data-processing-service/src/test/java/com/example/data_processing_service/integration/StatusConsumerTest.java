package com.example.data_processing_service.integration;

import com.example.data_processing_service.database.model.MarketModel;
import com.example.data_processing_service.database.repository.MarketModelRepository;
import com.example.data_processing_service.features.ingestion.client.IngestionClient;
import com.example.data_processing_service.features.orchestrator.service.OrchestratorService;
import com.example.data_processing_service.features.shared.dto.RawMarketDTO;
import com.example.data_processing_service.kafka.dto.EventDTO;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = {"status"}
)
@DirtiesContext
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
@ActiveProfiles("test")
public class StatusConsumerTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private OrchestratorService orchestratorService;

    @Autowired
    private MarketModelRepository repository;

    @MockitoBean
    private IngestionClient ingestionClient;

    private static final String TEST_TOPIC = "status";
    private static final Long TIMESTAMP = 123456789L;
    private static final String KEY = "Status Completed";
    private static final String GROUP_ID = "processing";
    private static final EventDTO EVENT_DTO = EventDTO.builder()
            .status(KEY)
            .timestamp(TIMESTAMP)
            .build();

    Producer<String, EventDTO> producer;
    private CountDownLatch latch;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void testReceiveStatus_ActivatesOrchestratorService() throws InterruptedException {
        producer = configureProducer();
        latch = new CountDownLatch(1);
        Set<RawMarketDTO> batch = new HashSet<>();
        while (batch.size() < 100) {
            batch.add(new RawMarketDTO(
                    "BTC",
                    1,
                    new BigDecimal("45000.50"),
                    new BigDecimal("45000.50"),
                    new BigDecimal("300000000.00"),
                    new BigDecimal("0.5"),
                    100000,
                    System.currentTimeMillis(),
                    "Binance",
                    "USDT",
                    "BTC",
                    "USDT",
                    TIMESTAMP
            ));
        }

        producer.send(new ProducerRecord<>(TEST_TOPIC, KEY, EVENT_DTO));

        when(ingestionClient.getRawMarketModels(TIMESTAMP)).thenReturn(batch);

        latch.await(5, TimeUnit.SECONDS);

        assertEquals(100, repository.findAll().size(), "Repository should have 100 markets saved");

        producer.close();
    }

    @KafkaListener(
            topics = TEST_TOPIC,
            groupId = GROUP_ID
    )
    public void receiveStatus(@Nonnull EventDTO status) {
        try {
            log.info("Status: {}, now starting orchestrator", status);
            orchestratorService.startProcessingFlow(status.getTimestamp());
        } catch (Exception ex) {
            log.error("Error initiating orchestration service at: {} for data fetched at: {}, {}",
                    LocalDateTime.now(), status.getTimestamp(), ex.getMessage());
            throw new KafkaException("Unexpected error occurred while receiving event status", ex);
        } finally {
            latch.countDown();
        }
    }

    private Producer<String, EventDTO> configureProducer() {
        Map<String, Object> producerProps = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<String, EventDTO>(producerProps).createProducer();
    }

    private Set<MarketModel> createMarketBatch() {
        Set<MarketModel> batch = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            batch.add(MarketModel.builder()
                    .id(UUID.randomUUID())
                    .baseId("BTC")
                    .priceUsd(new BigDecimal("45000.50"))
                    .updated(System.currentTimeMillis())
                    .exchangeId("Binance")
                    .quoteId("USDT")
                    .timestamp(ZonedDateTime.now())
                    .build());
        }
        return batch;
    }

    private Set<RawMarketDTO> createRawMarketBatch() {
        Set<RawMarketDTO> batch = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            batch.add(RawMarketDTO.builder()
                    .baseId("BTC")
                    .rank(1)
                    .priceQuote(new BigDecimal("45000.50"))
                    .priceUsd(new BigDecimal("45000.50"))
                    .volumeUsd24Hr(new BigDecimal("300000000.00"))
                    .percentExchangeVolume(new BigDecimal("0.5"))
                    .tradesCount24Hr(100000)
                    .updated(System.currentTimeMillis())
                    .exchangeId("Binance")
                    .quoteId("USDT")
                    .baseSymbol("BTC")
                    .quoteSymbol("USDT")
                    .timestamp(TIMESTAMP)
                    .build());
        }
        return batch;
    }
}
