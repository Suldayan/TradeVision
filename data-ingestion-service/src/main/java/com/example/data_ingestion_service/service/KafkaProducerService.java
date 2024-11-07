package com.example.data_ingestion_service.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
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

    private final KafkaTemplate<String, T> kafkaTemplate;

    /*
    * @Param: Takes a string for a topic and a generic, T, for data to represent multiple models
    * Responsible for sending data to the data processing microservice
    * */
    @Retry(name = "sendData", fallbackMethod = "sendDataFallback")
    @CircuitBreaker(name = "sendData")
    public void sendData(String topic, T data) {
        log.info("Sending data to consumer");

        try {
            kafkaTemplate.send(topic, data)
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

    /*
    * sendData fallback method
    * */
    public void sendDataFallback(Exception e) throws Exception {
        throw new Exception(String.format("Producer Fallback method initiated due to all retry attempts exhausted::%s",
                e.getMessage()));
    }
}