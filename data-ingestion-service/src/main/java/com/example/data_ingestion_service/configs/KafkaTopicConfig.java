package com.example.data_ingestion_service.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    private static final String TOPIC = "status";

    @Bean
    public NewTopic statusTopic() {
        return TopicBuilder.name(TOPIC)
                .build();
    }
}
