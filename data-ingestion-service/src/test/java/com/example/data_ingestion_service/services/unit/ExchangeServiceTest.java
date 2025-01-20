package com.example.data_ingestion_service.services.unit;

import com.example.data_ingestion_service.clients.ExchangeClient;
import com.example.data_ingestion_service.models.RawExchangesModel;
import com.example.data_ingestion_service.records.Exchange;
import com.example.data_ingestion_service.records.wrapper.ExchangeWrapper;
import com.example.data_ingestion_service.services.exceptions.ApiException;
import com.example.data_ingestion_service.services.impl.ExchangeServiceImpl;
import com.example.data_ingestion_service.services.mapper.ExchangeMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExchangeServiceTest {
    
    @Mock
    ExchangeClient exchangeClient;
    
    @Mock
    ExchangeMapper exchangeMapper;
    
    @InjectMocks
    ExchangeServiceImpl exchangeService;

    public static final Exchange exchange1 = new Exchange(
            "1",
            "Mock Exchange A",
            1,
            new BigDecimal("25.50"),
            new BigDecimal("100000000.00"),
            200,
            true,
            "https://www.mockexchangea.com",
            System.currentTimeMillis(),
            1737247412551L
    );

    public static final Exchange exchange2 = new Exchange(
            "2",
            "Mock Exchange B",
            2,
            new BigDecimal("15.75"),
            new BigDecimal("50000000.00"),
            150,
            false,
            "https://www.mockexchangeb.com",
            System.currentTimeMillis(),
            1737247412551L
    );

    public static final ExchangeWrapper mockExchangeWrapper = new ExchangeWrapper(Set.of(exchange1, exchange2), 124324353L);
    
    @Test
    void testGetExchangeData_ReturnsSetOfExchangeRecords() {
        when(exchangeClient.getExchanges()).thenReturn(mockExchangeWrapper);
        
        Set<Exchange> result = assertDoesNotThrow(() -> exchangeService.getExchangeData());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(exchange1));
        assertTrue(result.contains(exchange2));
        verify(exchangeClient).getExchanges();
    }

    @Test
    void getExchangeData_ThrowsException_WhenExchangeWrapperIsNull() {
        when(exchangeClient.getExchanges()).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> exchangeService.getExchangeData());
        assertEquals("Failed to fetch exchange wrapper data: com.example.data_ingestion_service.services.exceptions.ApiException: Exchanges data fetched but returned as null", exception.getMessage());
        verify(exchangeClient).getExchanges();
    }

    @Test
    void getExchangeData_ThrowsException_OnEmptyDataSet() {
        ExchangeWrapper exchangeWrapper = new ExchangeWrapper(new HashSet<>(), 12453254L);
        when(exchangeClient.getExchanges()).thenReturn(exchangeWrapper);

        ApiException exception = assertThrows(ApiException.class, () -> exchangeService.getExchangeData());
        assertTrue(exception.getMessage().contains("Exchange set fetched but is empty"));
        verify(exchangeClient).getExchanges();
    }

    @Test
    void getExchangeData_ThrowsException_OnClientError() {
        when(exchangeClient.getExchanges()).thenThrow(new RuntimeException("Client Error"));

        ApiException exception = assertThrows(ApiException.class, () -> exchangeService.getExchangeData());
        assertTrue(exception.getMessage().contains("Failed to fetch exchange wrapper data"));
        verify(exchangeClient).getExchanges();
    }

    @Test
    void convertToModel_ReturnsValidRawExchangeModelSet() {
        RawExchangesModel rawExchangesModel1 = RawExchangesModel.builder()
                .modelId(UUID.randomUUID().toString())
                .exchangeId(exchange1.exchangeId())
                .name(exchange1.name())
                .rank(exchange1.rank())
                .percentTotalVolume(exchange1.percentTotalVolume())
                .volumeUsd(exchange1.volumeUsd())
                .tradingPairs(exchange1.tradingPairs())
                .socket(Boolean.TRUE.equals(exchange1.socket()))
                .exchangeUrl(exchange1.exchangeUrl())
                .updated(exchange1.updated())
                .build();

        RawExchangesModel rawExchangesModel2 = RawExchangesModel.builder()
                .modelId(UUID.randomUUID().toString())
                .exchangeId(exchange2.exchangeId())
                .name(exchange2.name())
                .rank(exchange2.rank())
                .percentTotalVolume(exchange2.percentTotalVolume())
                .volumeUsd(exchange2.volumeUsd())
                .tradingPairs(exchange2.tradingPairs())
                .socket(Boolean.TRUE.equals(exchange2.socket()))
                .exchangeUrl(exchange2.exchangeUrl())
                .updated(exchange2.updated())
                .build();

        when(exchangeClient.getExchanges()).thenReturn(mockExchangeWrapper);
        when(exchangeMapper.exchangeRecordToEntity(exchangeService.getExchangeData())).thenReturn(Set.of(rawExchangesModel1, rawExchangesModel2));

        Set<RawExchangesModel> result = exchangeService.convertToModel();

        assertDoesNotThrow(() -> exchangeService.convertToModel());
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
