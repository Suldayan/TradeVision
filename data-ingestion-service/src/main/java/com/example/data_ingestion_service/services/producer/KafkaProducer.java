package com.example.data_ingestion_service.services.producer;

import com.example.data_ingestion_service.services.dto.EventDTO;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.type.SerializationException;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, EventDTO> kafkaTemplate;

    private static final String TOPIC = "status";

    public void sendMessage(@Nonnull EventDTO event) {
        try {
            log.info("Sending message to topic {}: {}", TOPIC, event);
            CompletableFuture<SendResult<String, EventDTO>> future = kafkaTemplate.send(TOPIC, event);
            future.thenAccept(result -> log.info("Sent message: {} with offset: {}", event, result.getRecordMetadata())).exceptionallyAsync(ex -> {
                log.error("Unable to send message at: {}, {}", LocalDateTime.now(), ex.getMessage());
                return null;
            });
        } catch (SerializationException ex) {
            log.error("Serialization error for event: {}", event, ex);
        } catch (KafkaException ex) {
            log.error("Failed to send event: {}", event, ex);
        }
    }
}
