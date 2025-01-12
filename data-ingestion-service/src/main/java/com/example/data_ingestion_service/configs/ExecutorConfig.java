package com.example.data_ingestion_service.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@Slf4j
public class ExecutorConfig {

    @Bean(name = "apiExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(3);
        executor.setThreadNamePrefix("AsyncApiThread-");
        executor.setRejectedExecutionHandler((r, ex) ->
                log.warn("Task rejected, que and thread limit reached: {}", ex.getQueue()));
        executor.initialize();
        return executor;
    }
}
