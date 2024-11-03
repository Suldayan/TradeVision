package com.example.data_ingestion_service.client;

import com.example.data_ingestion_service.excpetions.ServiceException;
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
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class ScheduledApiClient {

    private final WebClient webClient;
    private final Bucket bucket;
    private final KafkaProducerService producerService;
    private final AtomicReference<MarketResponseModel> lastKnownDataState;

    public ScheduledApiClient(KafkaProducerService producerService,
                              WebClient webClient,
                              Bucket bucket, AtomicReference<MarketResponseModel> lastKnownDataState) {
        this.producerService = producerService;
        this.webClient = webClient;
        this.bucket = bucket;
        this.lastKnownDataState = new AtomicReference<>();
    }

    // TODO: Add retries and circuit breaking
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
                .bodyToMono(MarketResponseModel.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .doBeforeRetry(x -> log.info("Retrying: {}", x.totalRetries()))
                        .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> {
                            throw new ServiceException(String.format("Service call failed, reaching retry count::%s", retrySignal.totalRetries()));
                        })));
    }

    private boolean hasDataChanged(MarketResponseModel data) {
        MarketResponseModel previousData = lastKnownDataState.get();

        if (previousData == null) {
            return true;
        }

        return !(data == previousData);
    }

    @Scheduled(fixedRate = 1000)
    public void sendToMarketProducer() {
        fetchMarketModelData()
                .doOnError(error -> log.error("Error fetching market data: {}", error.getMessage()))
                .subscribe(
                        data -> {
                            try {
                                if (hasDataChanged(data)) {
                                    log.info("Market data has changed, sending data: {}", LocalDateTime.now());
                                    producerService.sendMarketData(data);
                                }

                                log.info("Market data is unchanged as of: {}", LocalDateTime.now());
                            } catch (Exception e) {
                                log.error("Failed to send market data to the producer", e);
                            }
                        },
                        error -> log.error("Failed to process market data", error)
                );
    }
}