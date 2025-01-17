package com.example.data_ingestion_service.services.integration;

import com.example.data_ingestion_service.records.Asset;
import com.example.data_ingestion_service.services.AssetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AssetServiceTest {

    @Autowired
    AssetService assetService;

    @Test
    void getAssetData_ReturnsValid_SetOfAssetRecords() {
        Set<Asset> apiResponse = assetService.getAssetData();

        assertDoesNotThrow(() -> assetService.getAssetData());
        assertNotNull(apiResponse, "Response should not be null");
        assertEquals(100, apiResponse.size(), "Size of set should be 100");
    }
}
