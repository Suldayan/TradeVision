package com.example.data_processing_service.integration;

import com.example.data_processing_service.kafka.consumer.StatusConsumer;
import com.example.data_processing_service.kafka.dto.EventDTO;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@EnableKafka
@SpringBootTest(classes = {StatusConsumer.class})
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:3333",
                "port=3333"
        }
)
public class StatusConsumerTest {

    @Autowired
    EmbeddedKafka kafkaEmbedded;

    @Autowired
    KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private Producer<String, EventDTO> producer;
    private Consumer<String, EventDTO> consumer;

    @BeforeEach
    void setup() throws Exception {
        for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer,
                    kafkaEmbedded.partitions());
        }
    }

    @AfterEach
    void closeKafkaInstances() {
        producer.close();
    }


}
