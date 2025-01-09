package com.example.data_ingestion_service.services.unit;

import com.example.data_ingestion_service.clients.AssetClient;
import com.example.data_ingestion_service.records.Asset;
import com.example.data_ingestion_service.records.wrapper.AssetWrapper;
import com.example.data_ingestion_service.services.exceptions.ApiException;
import com.example.data_ingestion_service.services.impl.AssetServiceImpl;
import com.example.data_ingestion_service.services.mapper.AssetMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AssetServiceTest {

    @Mock
    private AssetClient assetClient;

    @Mock
    private AssetMapper assetMapper;

    @InjectMocks
    private AssetServiceImpl assetService;

    @Test
    void getAssetData_ReturnsValidAssets() {
        // Arrange
        Asset mockAsset1 = new Asset(
                "1",
                1,
                "BTC",
                "Bitcoin",
                new BigDecimal("21000000"),
                new BigDecimal("21000000"),
                new BigDecimal("400000000000"),
                new BigDecimal("1000000000"),
                new BigDecimal("20000"),
                5.0,
                new BigDecimal("19500"),
                "https://bitcoin.org"
        );

        Asset mockAsset2 = new Asset(
                "2",
                2,
                "ETH",
                "Ethereum",
                new BigDecimal("120000000"),
                new BigDecimal("120000000"),
                new BigDecimal("200000000000"),
                new BigDecimal("500000000"),
                new BigDecimal("1500"),
                3.0,
                new BigDecimal("1450"),
                "https://ethereum.org"
        );

        AssetWrapper mockAssetWrapper = new AssetWrapper(Set.of(mockAsset1, mockAsset2), 133234325L);
        when(assetClient.getAssets()).thenReturn(mockAssetWrapper);

        // Act
        Set<Asset> assets = assetService.getAssetData();

        // Assert
        assertNotNull(assets);
        assertEquals(2, assets.size());
        assertTrue(assets.contains(mockAsset1));
        assertTrue(assets.contains(mockAsset2));
        verify(assetClient).getAssets();
    }


    @Test
    void getAssetData_ThrowsException_WhenAssetWrapperIsNull() {
        // Arrange
        when(assetClient.getAssets()).thenReturn(null);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () -> assetService.getAssetData());
        assertEquals("Failed to fetch asset wrapper data: Asset wrapper model fetched but returned as null", exception.getMessage());
        verify(assetClient).getAssets();
    }

    @Test
    void getAssetData_ThrowsException_OnClientError() {
        // Arrange
        when(assetClient.getAssets()).thenThrow(new RuntimeException("Client Error"));

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () -> assetService.getAssetData());
        assertTrue(exception.getMessage().contains("Failed to fetch asset wrapper data"));
        verify(assetClient).getAssets();
    }
}
