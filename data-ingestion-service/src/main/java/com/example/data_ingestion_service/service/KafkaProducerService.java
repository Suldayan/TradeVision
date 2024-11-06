package com.example.data_ingestion_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService<T> {

    @Value("${raw-data.market}")
    private String marketTopic;

    private final KafkaTemplate<String, T> kafkaTemplate;

    public void sendData(T data) {
        log.info("Sending data to consumer");

        try {
            kafkaTemplate.send(marketTopic, data)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Data successfully sent to topic {} at partition {} with offset {}",
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        } else {
                            log.error("Failed to send data to consumer", ex);
                        }
                    });
        } catch (KafkaException e) {
            log.error("Failed to send data to consumer", e);
            throw e;
        }
    }
}