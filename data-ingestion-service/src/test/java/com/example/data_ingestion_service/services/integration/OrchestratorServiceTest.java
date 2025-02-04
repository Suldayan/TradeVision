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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka(partitions = 1,
        topics = {"status"}
)
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
    private BlockingQueue<EventDTO> receivedEvents;

    @BeforeEach
    void setUp() {
        receivedEvents = new LinkedBlockingQueue<>();
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

        EventDTO receivedEvent = receivedEvents.poll(10, TimeUnit.SECONDS);

        assertNotNull(receivedEvent, "Should have received a message");
        System.out.printf("Received event status from producer: %s%n", receivedEvent.getStatus());
        System.out.println(receivedEvent.getTimestamp());

        List<RawMarketModel> savedModels = repository.findAll();
        assertFalse(savedModels.isEmpty(), "Database should contain saved models");
        assertEquals(savedModels.size(), 100, "Model list size should be 100");

        assertEquals("Completed successfully", receivedEvent.getStatus());
        assertNotNull(receivedEvent.getTimestamp());
    }
}