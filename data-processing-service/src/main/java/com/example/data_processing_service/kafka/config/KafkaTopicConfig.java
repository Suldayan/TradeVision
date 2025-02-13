package com.example.data_processing_service.kafka.config;

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
