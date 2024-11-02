package com.example.data_ingestion_service.client;

import com.example.data_ingestion_service.model.MarketResponseModel;
import com.example.data_ingestion_service.service.KafkaProducerService;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@Slf4j
public class ScheduledApiClient {

    private final WebClient webClient;
    private final Bucket bucket;
    private final KafkaProducerService producerService;

    public ScheduledApiClient(KafkaProducerService producerService,
                              WebClient webClient,
                              Bucket bucket) {
        this.producerService = producerService;
        this.webClient = webClient;
        this.bucket = bucket;
    }

    @Cacheable(value = "market_data", key = "#id")
    public Mono<MarketResponseModel> fetchMarketModelData() {
        if (!bucket.tryConsume(1)) {
            log.warn("Rate limit exceeded. Available tokens: {}", bucket.getAvailableTokens());
            return Mono.error(new RuntimeException("Rate limit exceeded"));
        }

        log.info("Fetching market data. Requests remaining: {}", bucket.getAvailableTokens());

        return webClient.get()
                .uri("/markets")
                .header("Accept-Encoding", "gzip")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Client error: " + errorBody)))
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Server error: " + errorBody)))
                )
                .bodyToMono(MarketResponseModel.class);
    }

    @CachePut(value = "market_data", key = "#id")
    public MarketResponseModel cachedMarketData(MarketResponseModel data) throws Exception {
        try {
            log.info("Caching market data at: {}", LocalDateTime.now());
            return data;
        } catch (Exception e) {
            log.error("Error updating cache for market data: {}", e.getMessage());
            throw new Exception("Failed to update cache", e);
        }
    }

    @Scheduled(fixedRate = 1000)
    public void sendToMarketProducer() {
        fetchMarketModelData()
                .doOnError(error -> log.error("Error fetching market data: {}", error.getMessage()))
                .subscribe(
                        data -> {
                            try {
                                producerService.sendMarketData(data);
                            } catch (Exception e) {
                                log.error("Failed to send market data to the producer", e);

                                log.info("Sending cached data instead");
                                producerService.sendMarketData(cachedMarketData(data));
                            }
                        },
                        error -> log.error("Failed to process market data", error)
                );
    }
}