package com.example.data_ingestion_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic rawAssetTopic() {
        return TopicBuilder
                .name("raw_assets")
                .build();
    }

    @Bean
    public NewTopic rawCandlesTopic() {
        return TopicBuilder
                .name("raw_candles")
                .build();
    }

    @Bean
    public NewTopic rawMarketTopic() {
        return TopicBuilder
                .name("raw_market")
                .build();
    }
}
