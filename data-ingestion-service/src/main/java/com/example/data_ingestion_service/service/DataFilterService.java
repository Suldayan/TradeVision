package com.example.data_ingestion_service.service;

import com.example.data_ingestion_service.model.MarketResponseModel;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class DataFilterService<T> {

    private final KafkaProducerService<T> producerService;
    private final AtomicReference<Object> previousModelState;
    private List<String> topics;

    @Value("${raw-data.market}")
    private String marketTopic;

    @Value("${raw-data.candle")
    private String candleTopic;

    public DataFilterService(KafkaProducerService<T> producerService) {
        this.producerService = producerService;
        this.topics = new ArrayList<>();
        this.previousModelState = new AtomicReference<>();
    }

    public void topics() {
        topics = List.of(
                marketTopic,
                candleTopic);
    }

    /*
    * Matches the response with its corresponding topic for kafka to know what topic to send to
    *
    * */
    public void addTopicToData(T response) {
        try {
            if (response instanceof MarketResponseModel) {
                log.debug("Adding topic: {} to match response: {}", topics.getFirst(), response.getClass());
                producerService.sendData(topics.getFirst(), response);
            }
        } catch (Exception e) {
            log.error(String.format("Failed to add topic to response::%s - %s", response, e.getMessage()));
        }
    }

    /*
    * Filters out responses that contain the same state as the last fetched data
    * It only sends data that have been changed and updates the last state
    * */
    public void filter(T response) {
        try {
            if (!response.equals(previousModelState.get())) {
                previousModelState.set(response);
                addTopicToData(response);
            }

            log.info("Data is unchanged as of: {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("Unable to compare response with its previous state: {}", e.getMessage());
        }
    }
}