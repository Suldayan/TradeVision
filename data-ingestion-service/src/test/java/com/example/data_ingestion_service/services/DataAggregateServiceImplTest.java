package com.example.data_ingestion_service.services;

import com.example.data_ingestion_service.models.raw.RawMarketModel;
import com.example.data_ingestion_service.models.raw.RawExchangesModel;
import com.example.data_ingestion_service.models.raw.RawAssetModel;
import com.example.data_ingestion_service.services.exceptions.DataAggregateException;
import com.example.data_ingestion_service.services.impl.DataAggregateServiceImpl;
import com.example.data_ingestion_service.services.impl.MarketServiceImpl;
import com.example.data_ingestion_service.services.impl.ExchangeServiceImpl;
import com.example.data_ingestion_service.services.impl.AssetServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DataAggregateServiceImplTest {
    @Mock
    private MarketServiceImpl marketService;
    @Mock
    private ExchangeServiceImpl exchangeService;
    @Mock
    private AssetServiceImpl assetService;

    @InjectMocks
    private DataAggregateServiceImpl dataAggregateService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testConfigureDataTypes() {
        // Prepare mock return values
        List<RawMarketModel> marketList = new ArrayList<>();
        List<RawExchangesModel> exchangeList = new ArrayList<>();
        List<RawAssetModel> assetList = new ArrayList<>();

        marketList.add(new RawMarketModel());
        exchangeList.add(new RawExchangesModel());
        assetList.add(new RawAssetModel());

        // Manually invoke the PostConstruct method using reflection
        ReflectionTestUtils.invokeMethod(dataAggregateService, "configureDataTypes");

        // Retrieve the dataTypes set using reflection
        List<List<?>> dataTypes = (List<List<?>>) ReflectionTestUtils.getField(dataAggregateService, "dataTypes");

        // Assert
        assertNotNull(dataTypes, "DataTypes set should not be null");
        assertEquals(3, dataTypes.size(), "DataTypes set should contain 3 lists");

        // Optional: Print out the contents for debugging
        dataTypes.forEach(list -> System.out.println("List size: " + list.size()));
    }

    @Test
    public void testDetectDataType() {
        // Define mock models
        RawMarketModel marketModel = new RawMarketModel();
        RawExchangesModel exchangesModel = new RawExchangesModel();
        RawAssetModel assetModel = new RawAssetModel();

        // Test a list of generics to simulate unknown data being passed onto the function
        List<Object> types = new ArrayList<>();
        types.add(marketModel);
        types.add(exchangesModel);
        types.add(assetModel);

        // Add non valid data type
        Object invalidModel = new Object();
        types.add(invalidModel);

        // Stream each model onto the function
        types.forEach(type -> {
            assertDoesNotThrow(() -> dataAggregateService.detectDataType(type));
        });
    }

    @Test
    public void testFetchDataAsync() throws DataAggregateException {
        // Prepare mock return values
        List<RawMarketModel> marketList = new ArrayList<>();
        List<RawExchangesModel> exchangeList = new ArrayList<>();
        List<RawAssetModel> assetList = new ArrayList<>();

        marketList.add(new RawMarketModel());
        exchangeList.add(new RawExchangesModel());
        assetList.add(new RawAssetModel());

        // Manually invoke the PostConstruct method to set up dataTypes
        ReflectionTestUtils.invokeMethod(dataAggregateService, "configureDataTypes");

        // Call fetchDataAsync and verify no exceptions
        assertDoesNotThrow(() -> dataAggregateService.fetchDataAsync());
    }
    /*
    @Test
    public void testFetchDataAsyncCircuitBreaking() throws DataAggregateException {
        for (int i = 1; i < 10; i++) {
            dataAggregateService.fetchDataAsync();
        }
    }
     */
}