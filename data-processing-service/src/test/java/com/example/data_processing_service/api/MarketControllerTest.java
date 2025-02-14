package com.example.data_processing_service.api;

import com.example.data_processing_service.controller.MarketController;
import com.example.data_processing_service.database.model.MarketModel;
import com.example.data_processing_service.database.repository.MarketModelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MarketControllerTest {

    @Mock
    private MarketModelRepository repository;

    @InjectMocks
    private MarketController marketController;

    private MockMvc mvc;

    private JacksonTester<Set<MarketModel>> jsonMarketModel;

    private final ZonedDateTime TIMESTAMP = ZonedDateTime.now().minusMonths(6);

    private final Set<MarketModel> batch = new HashSet<>();

    private static final String BASE_URL = "/api/v1/processing";

    @BeforeEach
    public void deleteAllModelsInDatabase() {
        repository.deleteAll();
    }

    @BeforeEach
    public void setup() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        JacksonTester.initFields(this, objectMapper);
        mvc = MockMvcBuilders.standaloneSetup(marketController)
                .build();

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
    void canRetrieveByTimestampWhenExistsOnAllEndpoint() throws Exception {
        long startDateMillis = Instant.now().minusSeconds(31536000).toEpochMilli();
        // Seconds to subtract is equal to a year
        long endDateMillis = Instant.now().toEpochMilli();

        ZonedDateTime zonedStartDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startDateMillis), ZoneOffset.UTC);
        ZonedDateTime zonedEndDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(endDateMillis), ZoneOffset.UTC);

        given(repository.findAllByTimestampBetween(zonedStartDate ,zonedEndDate))
                .willReturn(batch);

        MockHttpServletResponse response = mvc.perform(
                MockMvcRequestBuilders.get(BASE_URL + "/all")
                        .param("startDate", String.valueOf(startDateMillis))
                        .param("endDate", String.valueOf(endDateMillis))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();

        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertNotNull(responseContent);
        assertThat(responseContent).isEqualTo(
                jsonMarketModel.write(batch).getJson()
        );
    }

    @Test
    void canRetrieveByTimestampAndBaseId() throws Exception {
        long startDateMillis = Instant.now().minusSeconds(31536000).toEpochMilli();
        long endDateMillis = Instant.now().toEpochMilli();

        final String baseId = "BTC";

        ZonedDateTime zonedStartDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startDateMillis), ZoneOffset.UTC);
        ZonedDateTime zonedEndDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(endDateMillis), ZoneOffset.UTC);

        given(repository.findByBaseIdAndTimestampBetween(baseId, zonedStartDate ,zonedEndDate))
                .willReturn(batch);

        MockHttpServletResponse response = mvc.perform(
                        MockMvcRequestBuilders.get(BASE_URL + "/base/" + baseId)
                                .param("startDate", String.valueOf(startDateMillis))
                                .param("endDate", String.valueOf(endDateMillis))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();

        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertNotNull(responseContent);
        assertThat(responseContent).isEqualTo(
                jsonMarketModel.write(batch).getJson()
        );
    }

    @Test
    void canRetrieveByTimestampAndQuoteId() throws Exception {
        long startDateMillis = Instant.now().minusSeconds(31536000).toEpochMilli();
        long endDateMillis = Instant.now().toEpochMilli();

        final String quoteId = "Binance";

        ZonedDateTime zonedStartDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startDateMillis), ZoneOffset.UTC);
        ZonedDateTime zonedEndDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(endDateMillis), ZoneOffset.UTC);

        given(repository.findByQuoteIdAndTimestampBetween(quoteId, zonedStartDate ,zonedEndDate))
                .willReturn(batch);

        MockHttpServletResponse response = mvc.perform(
                        MockMvcRequestBuilders.get(BASE_URL + "/quote/" + quoteId)
                                .param("startDate", String.valueOf(startDateMillis))
                                .param("endDate", String.valueOf(endDateMillis))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();

        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertNotNull(responseContent);
        assertThat(responseContent).isEqualTo(
                jsonMarketModel.write(batch).getJson()
        );
    }

    @Test
    void canRetrieveByTimestampAndExchangeId() throws Exception {
        long startDateMillis = Instant.now().minusSeconds(31536000).toEpochMilli();
        long endDateMillis = Instant.now().toEpochMilli();

        final String exchangeId = "USDT";

        ZonedDateTime zonedStartDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startDateMillis), ZoneOffset.UTC);
        ZonedDateTime zonedEndDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(endDateMillis), ZoneOffset.UTC);

        given(repository.findByQuoteIdAndTimestampBetween(exchangeId, zonedStartDate ,zonedEndDate))
                .willReturn(batch);

        MockHttpServletResponse response = mvc.perform(
                        MockMvcRequestBuilders.get(BASE_URL + "/exchange/" + exchangeId)
                                .param("startDate", String.valueOf(startDateMillis))
                                .param("endDate", String.valueOf(endDateMillis))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();

        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertNotNull(responseContent);
        assertThat(responseContent).isEqualTo(
                jsonMarketModel.write(batch).getJson()
        );
    }
}
