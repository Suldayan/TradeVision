package com.example.data_ingestion_service.service;

import com.example.data_ingestion_service.model.MarketResponseModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {

    @Value("${raw-data.market}")
    private String marketTopic;

    private final KafkaTemplate<String, MarketResponseModel> kafkaTemplate;

    public void sendMarketData(MarketResponseModel marketData) {
        log.info("Sending market data to consumer");

        try {
            kafkaTemplate.send(marketTopic, marketData)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Data successfully sent to topic {} at partition {} with offset {}",
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        } else {
                            log.error("Failed to send market data to consumer", ex);
                        }
                    });
        } catch (KafkaException e) {
            log.error("Failed to send market data to consumer", e);
            throw e;
        }
    }
}