package com.example.data_processing_service.integration;

import com.example.data_processing_service.database.model.MarketModel;
import com.example.data_processing_service.database.repository.MarketModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MarketControllerTest {

    @MockitoBean
    private MarketModelRepository repository;

    @Autowired
    private TestRestTemplate template;

    private final Set<MarketModel> batch = new HashSet<>();

    private static final ZonedDateTime TIMESTAMP =  ZonedDateTime.now().minusMonths(6);

    private static final String BASE_URL = "/api/v1/processing";

    @BeforeEach
    void setupBatch() {
        for (int i = 0; i < 100; i++) {
            MarketModel model = MarketModel.builder()
                    .id(UUID.randomUUID())
                    .baseId("BTC")
                    .priceUsd(new BigDecimal("45000.50"))
                    .updated(System.currentTimeMillis())
                    .exchangeId("Binance")
                    .quoteId("USDT")
                    .timestamp(TIMESTAMP)
                    .createdAt(Instant.now())
                    .build();
            batch.add(model);
        }
    }

    @Test
    void canRetrieveByTimestampWhenExistsOnAllEndpoint() {
        long startDateMillis = Instant.now().minusSeconds(31536000).toEpochMilli();
        long endDateMillis = Instant.now().toEpochMilli();

        ZonedDateTime zonedStartDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startDateMillis), ZoneOffset.UTC);
        ZonedDateTime zonedEndDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(endDateMillis), ZoneOffset.UTC);

        String url = UriComponentsBuilder.fromPath(BASE_URL + "/all")
                        .queryParam("startDate", startDateMillis)
                        .queryParam("endDate", endDateMillis)
                        .toUriString();

        given(repository.findAllByTimestampBetween(zonedStartDate, zonedEndDate))
                .willReturn(batch);

        ResponseEntity<Set<MarketModel>> marketModelResponse = template.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(marketModelResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(marketModelResponse.getBody()).isEqualTo(batch);
    }

    @Test
    void canRetrieveByTimestampAndBaseId() {
        long startDateMillis = Instant.now().minusSeconds(31536000).toEpochMilli();
        long endDateMillis = Instant.now().toEpochMilli();

        final String baseId = "BTC";

        ZonedDateTime zonedStartDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startDateMillis), ZoneOffset.UTC);
        ZonedDateTime zonedEndDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(endDateMillis), ZoneOffset.UTC);

        String url = UriComponentsBuilder.fromPath(BASE_URL + "/base/" + baseId)
                .queryParam("startDate", startDateMillis)
                .queryParam("endDate", endDateMillis)
                .toUriString();

        given(repository.findByBaseIdAndTimestampBetween(baseId, zonedStartDate, zonedEndDate))
                .willReturn(batch);

        ResponseEntity<Set<MarketModel>> marketModelResponse = template.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(marketModelResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(marketModelResponse.getBody()).isEqualTo(batch);
    }

    @Test
    void canRetrieveByTimestampAndQuoteId() {
        long startDateMillis = Instant.now().minusSeconds(31536000).toEpochMilli();
        long endDateMillis = Instant.now().toEpochMilli();

        final String quoteId = "USDT";

        ZonedDateTime zonedStartDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startDateMillis), ZoneOffset.UTC);
        ZonedDateTime zonedEndDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(endDateMillis), ZoneOffset.UTC);

        String url = UriComponentsBuilder.fromPath(BASE_URL + "/quote/" + quoteId)
                .queryParam("startDate", startDateMillis)
                .queryParam("endDate", endDateMillis)
                .toUriString();

        given(repository.findByQuoteIdAndTimestampBetween(quoteId, zonedStartDate, zonedEndDate))
                .willReturn(batch);

        ResponseEntity<Set<MarketModel>> marketModelResponse = template.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(marketModelResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(marketModelResponse.getBody()).isEqualTo(batch);
    }

    @Test
    void canRetrieveByTimestampAndExchangeId() {
        long startDateMillis = Instant.now().minusSeconds(31536000).toEpochMilli();
        long endDateMillis = Instant.now().toEpochMilli();

        final String exchangeId = "Binance";

        ZonedDateTime zonedStartDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startDateMillis), ZoneOffset.UTC);
        ZonedDateTime zonedEndDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(endDateMillis), ZoneOffset.UTC);

        String url = UriComponentsBuilder.fromPath(BASE_URL + "/exchange/" + exchangeId)
                .queryParam("startDate", startDateMillis)
                .queryParam("endDate", endDateMillis)
                .toUriString();

        given(repository.findByExchangeIdAndTimestampBetween(exchangeId, zonedStartDate, zonedEndDate))
                .willReturn(batch);

        ResponseEntity<Set<MarketModel>> marketModelResponse = template.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(marketModelResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(marketModelResponse.getBody()).isEqualTo(batch);
    }
}
