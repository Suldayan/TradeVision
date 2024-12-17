package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.clients.MarketClient;
import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.models.RawMarketWrapperModel;
import com.example.data_ingestion_service.services.impl.MarketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

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
    void testGetMarketData() {
        //RawMarketWrapperModel listResponse = new RawMarketWrapperModel();
        List<RawMarketModel> response = new ArrayList<>();
        RawMarketModel marketModel = new RawMarketModel();
        response.add(marketModel);

        //when(marketService.getMarketData()).thenReturn(response);
        when(marketService.getMarketsAsList()).thenReturn(response);

        //assertEquals(marketService.getMarketData(), response, "Market api responses should match");
        assertEquals(marketService.getMarketsAsList(), response, "Market list api response should match");
    }
}
