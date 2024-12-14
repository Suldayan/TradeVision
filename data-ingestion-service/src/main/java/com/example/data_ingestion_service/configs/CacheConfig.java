package com.example.data_ingestion_service.configs;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "ingestion.cache.enabled", havingValue = "true")
@EnableCaching
public class CacheConfig {
}
