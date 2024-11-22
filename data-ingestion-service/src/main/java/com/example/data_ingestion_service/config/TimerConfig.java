package com.example.data_ingestion_service.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TimerConfig {

    private final MeterRegistry meterRegistry;

    @Bean
    public Timer timer() {
        return Timer.builder("api-response-timer")
                .tags("response", "api")
                .publishPercentiles(0.95)
                .register(meterRegistry);
    }
}
