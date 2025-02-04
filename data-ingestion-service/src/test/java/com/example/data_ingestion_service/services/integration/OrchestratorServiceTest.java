package com.example.data_ingestion_service.services.integration;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.repository.RawMarketModelRepository;
import com.example.data_ingestion_service.services.OrchestratorService;
import com.example.data_ingestion_service.services.dto.EventDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka(partitions = 1,
        topics = {"status"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext
@ActiveProfiles("test")
public class OrchestratorServiceTest {

    @Autowired
    private OrchestratorService orchestratorService;

    @Autowired
    private RawMarketModelRepository repository;

    @Autowired
    private ConsumerFactory<String, EventDTO> consumerFactory;

    private ConcurrentMessageListenerContainer<String, EventDTO> container;

    private static final String KAFKA_TOPIC = "status";
    private List<EventDTO> receivedEvents;
    private CountDownLatch latch;

    @BeforeEach
    void setUp() {
        receivedEvents = new ArrayList<>();
        latch = new CountDownLatch(1);
        repository.deleteAll();
        setupKafkaConsumer();
    }

    @AfterEach
    void tearDown() {
        if (container != null) {
            container.stop();
        }
    }

    private void setupKafkaConsumer() {
        ContainerProperties containerProps = new ContainerProperties(KAFKA_TOPIC);
        containerProps.setMessageListener((MessageListener<String, EventDTO>) record -> {
            receivedEvents.add(record.value());
            latch.countDown();
        });

        container = new ConcurrentMessageListenerContainer<>(
                consumerFactory,
                containerProps
        );
        container.start();
    }

    @Test
    void fullPipelineIntegrationTest() throws Exception {
        orchestratorService.executeDataPipeline();

        boolean messageReceived = latch.await(10, TimeUnit.SECONDS);

        assertTrue(messageReceived, "Kafka message should have been received");

        List<RawMarketModel> savedModels = repository.findAll();
        assertFalse(savedModels.isEmpty(), "Database should contain saved models");

        assertFalse(receivedEvents.isEmpty(), "Should have received pipeline completion event");
        EventDTO receivedEvent = receivedEvents.getFirst();
        assertEquals("Completed successfully", receivedEvent.getStatus());
        assertNotNull(receivedEvent.getTimestamp());
    }
}
