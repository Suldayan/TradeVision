package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.clients.MarketClient;
import com.example.data_ingestion_service.models.raw.RawMarketModel;
import com.example.data_ingestion_service.models.RawMarketWrapperModel;
import com.example.data_ingestion_service.services.impl.MarketServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ApiUnitTest {
    @Mock
    private MarketClient marketClient;

    @InjectMocks
    private MarketServiceImpl marketService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMarketData() throws JsonProcessingException {
        // Define the json mock response from the api
        String mockApiResponse = """
            {
              "data": [
                {
                    "exchangeId": "alterdice",
                    "rank": "1",
                    "baseSymbol": "BTC",
                    "baseId": "bitcoin",
                    "quoteSymbol": "USDT",
                    "quoteId": "tether",
                    "priceQuote": "100796.2900000000000000",
                    "priceUsd": "100735.4233393724281601",
                    "volumeUsd24Hr": "43689790.1229918912724990",
                    "percentExchangeVolume": "100.0000000000000000",
                    "tradesCount24Hr": "7",
                    "updated": 1734569938311
                }
               ],
              "timestamp": 344232
            }
            """;
        //TODO current issue: wrapper returns as null despite the json inside data being matched to the raw market model
        // Read the values onto the wrapper model object
        ObjectMapper mapper = new ObjectMapper();
        RawMarketWrapperModel wrapperModelResponse = mapper.readValue(mockApiResponse, RawMarketWrapperModel.class);

        assertDoesNotThrow(() -> {
            List<RawMarketModel> result = marketService.getMarketsData();
            assertEquals(result, wrapperModelResponse.getMarketModelList(), "Market api responses should match");
        });
    }
}
