package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.clients.MarketClient;
import com.example.data_ingestion_service.models.RawMarketModel;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
        // Read the values onto the wrapper model object
        ObjectMapper mapper = new ObjectMapper();
        /*
        * TODO fixed the json issue but bumped into a new one where the raw market wrapper model always returns as null.
        *  Figured that this could be due to the fact that the issue previously fixed exposes the market model and not the wrapper model.
        *  Because of this, we could change the getMarketData() function to return the list of market models as it's properties are connected to the function
        * */
        RawMarketWrapperModel wrapperModelResponse = mapper.readValue(mockApiResponse, RawMarketWrapperModel.class);

        when(marketService.getMarketData()).thenReturn(wrapperModelResponse);
        assertDoesNotThrow(() -> {
            RawMarketWrapperModel result = marketService.getMarketData();
            assertEquals(marketService.getMarketData(), wrapperModelResponse, "Market api responses should match");
        });
    }
}
