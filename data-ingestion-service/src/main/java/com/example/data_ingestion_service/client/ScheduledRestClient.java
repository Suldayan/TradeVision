package com.example.data_ingestion_service.client;

import com.example.data_ingestion_service.model.MarketModel;
import com.example.data_ingestion_service.service.DataFilterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ScheduledRestClient<T> {

    private final RestClient restClient;
    private final Bucket bucket;
    private final DataFilterService<T> filterService;
    private final ObjectMapper objectMapper;
    private final Map<String, Class<T>> dataTypeToModel;

    public ScheduledRestClient(RestClient restClient, Bucket bucket, DataFilterService<T> filterService) {
        this.restClient = restClient;
        this.bucket = bucket;
        this.filterService = filterService;
        this.objectMapper = new ObjectMapper();
        this.dataTypeToModel = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public void defineTypeModelMap() {
        dataTypeToModel.put("market", (Class<T>) MarketModel.class);
        // Add any other data type to model mappings here
    }

    /*
    *
    * */
    @Scheduled(fixedRate = 1000)
    public void scheduleFetch() {
        dataTypeToModel.entrySet().stream()
                .map(entry -> CompletableFuture
                        .supplyAsync(() -> fetchData(entry.getKey(), entry.getValue()))
                        .thenAccept(filterService::filter)
                        .thenRun(this::updateBucket)
                        .exceptionally(throwable -> {
                            log.error("Error scheduling fetch for data type: {}, {}",
                                    entry.getKey(), throwable.getMessage());
                            return null;
                        }))
                .forEach(CompletableFuture::join);
    }

    /*
    * fetches the data from the coin cap api with an expected response of json
    * @Returns a generic of R to generalize the 3 data models being worked with
    * */
    public <R> R fetchData(String dataType, Class<R> responseType) {
        log.info("Fetching data of type: {}", responseType);
        return restClient.get()
                .uri("/{dataType}", dataType)
                .accept(MediaType.APPLICATION_JSON)
                .exchange((request, response) -> {
                    if (response.getStatusCode().is5xxServerError()) {
                        // TODO: add retrying
                    }
                    return convertResponse(response, responseType);
                });
    }

    /*
    * keeps track of the current rate limit
    * TODO: add circuit breaking to -> open state the function if it reaches the limit
    * */
    public void updateBucket() {
        if (!bucket.tryConsume(1)) {
            log.warn("Rate limit has been reached");
        }
    }

    /*
    * converts the response to its respective data model
    * @Return: returns generic type of R as we're working with 3 different models and using a centralized function
    * */
    public <R> R convertResponse(ClientHttpResponse response, Class<R> responseType) throws IOException {
        try {
            return objectMapper.readValue(response.getBody(), responseType);
        } catch (IOException e) {
            throw new IOException(String.format("Failed to convert response of type::%s, %s",
                    responseType, e.getMessage()));
        }
    }
}