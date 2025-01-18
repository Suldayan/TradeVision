package com.example.data_ingestion_service.services.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.type.SerializationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String msg) {
        try {
            log.info("Sending message to status topic: {}", msg);
            kafkaTemplate.send("status" ,msg);
        } catch (SerializationException ex) {
            log.error("Serialization error occurred while sending message to kafka");
        }
    }
}
