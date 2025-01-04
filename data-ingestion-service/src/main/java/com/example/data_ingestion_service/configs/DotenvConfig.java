package com.example.data_ingestion_service.configs;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    @PostConstruct
    public void loadEnv() {
        Dotenv dotenv = Dotenv.load();
        dotenv.entries()
                .forEach(entry -> {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    System.setProperty(key, value);
                });
    }
}
