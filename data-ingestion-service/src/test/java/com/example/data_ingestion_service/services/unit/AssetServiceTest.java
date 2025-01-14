package com.example.data_ingestion_service.services.unit;

import com.example.data_ingestion_service.clients.AssetClient;
import com.example.data_ingestion_service.models.RawAssetModel;
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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    public static final Asset mockAsset1 = new Asset(
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

    public static final Asset mockAsset2 = new Asset(
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

    public static final AssetWrapper mockAssetWrapper = new AssetWrapper(Set.of(mockAsset1, mockAsset2), 133234325L);

    @Test
    void getAssetData_ReturnsValidAssets() {
        when(assetClient.getAssets()).thenReturn(mockAssetWrapper);

        Set<Asset> assets = assetService.getAssetData();

        assertDoesNotThrow(() -> assets);
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
    void getAssetData_ThrowsException_OnEmptyDataSet() {
        // Arrange
        AssetWrapper assetWrapper = new AssetWrapper(new HashSet<>(), 12323425L);
        when(assetClient.getAssets()).thenReturn(assetWrapper);

        ApiException exception = assertThrows(ApiException.class, () -> assetService.getAssetData());
        assertTrue(exception.getMessage().contains("Asset set fetched but is empty"));
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

    @Test
    void convertToModel_ReturnsValidRawAssetModelSet() {
        RawAssetModel rawAssetModel1 = RawAssetModel.builder()
                .modelId(UUID.randomUUID().toString())
                .id(mockAsset1.id())
                .rank(mockAsset1.rank())
                .symbol(mockAsset1.symbol())
                .name(mockAsset1.name())
                .supply(mockAsset1.supply())
                .maxSupply(mockAsset1.maxSupply())
                .marketCapUsd(mockAsset1.marketCapUsd())
                .volumeUsd24Hr(mockAsset1.volume24Hr())
                .priceUsd(mockAsset1.priceUsd())
                .changePercent24Hr(BigDecimal.valueOf(mockAsset1.changePercent24Hr()))
                .vwap24Hr(mockAsset1.vwap24Hr())
                .explorer(mockAsset1.explorer())
                .build();

        RawAssetModel rawAssetModel2 = RawAssetModel.builder()
                .modelId(UUID.randomUUID().toString())
                .id(mockAsset2.id())
                .rank(mockAsset2.rank())
                .symbol(mockAsset2.symbol())
                .name(mockAsset2.name())
                .supply(mockAsset2.supply())
                .maxSupply(mockAsset2.maxSupply())
                .marketCapUsd(mockAsset2.marketCapUsd())
                .volumeUsd24Hr(mockAsset2.volume24Hr())
                .priceUsd(mockAsset2.priceUsd())
                .changePercent24Hr(BigDecimal.valueOf(mockAsset2.changePercent24Hr()))
                .vwap24Hr(mockAsset2.vwap24Hr())
                .explorer(mockAsset2.explorer())
                .build();

        when(assetClient.getAssets()).thenReturn(mockAssetWrapper);
        when(assetMapper.assetRecordToEntity(assetService.getAssetData())).thenReturn(Set.of(rawAssetModel1, rawAssetModel2));

        Set<RawAssetModel> result = assetService.convertToModel();

        assertDoesNotThrow(() -> assetService.convertToModel());
        assertNotNull(result);
        assertEquals(2, result.size());

        System.out.println(result);
    }
}
