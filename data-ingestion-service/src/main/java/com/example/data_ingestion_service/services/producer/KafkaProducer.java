package com.example.data_ingestion_service.services.producer;

import com.example.data_ingestion_service.services.dto.EventDTO;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.type.SerializationException;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, EventDTO> kafkaTemplate;

    private static final String TOPIC = "status";

    public void sendMessage(@Nonnull EventDTO event) {
        try {
            log.info("Sending message to topic {}: {}", TOPIC, event);
            Message<EventDTO> eventDTOMessage = buildJsonMessage(event);
            CompletableFuture<SendResult<String, EventDTO>> future = kafkaTemplate.send(eventDTOMessage);
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

    private Message<EventDTO> buildJsonMessage(@Nonnull EventDTO event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .build();
    }
}
