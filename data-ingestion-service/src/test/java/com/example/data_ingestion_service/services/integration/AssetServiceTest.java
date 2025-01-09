package com.example.data_ingestion_service.services.integration;

import com.example.data_ingestion_service.models.RawAssetModel;
import com.example.data_ingestion_service.records.Asset;
import com.example.data_ingestion_service.services.impl.AssetServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AssetServiceTest {

    @Autowired
    AssetServiceImpl assetService;

    @Test
    void testGetAssetData_ReturnsValidSetOfAssets() {
        Set<Asset> apiResponse = assetService.getAssetData();

        assertDoesNotThrow(() -> assetService.getAssetData(), "Api Exceptions should not be thrown");
        assertNotNull(apiResponse, "Asset data should not be null");
        assertEquals(apiResponse.size(), 100, "Response size should be a set of 100 total assets");

        // For confirming assets within the set
        for (Asset asset : apiResponse) {
            System.out.println(asset);
        }
    }

    @Test
    void testConvertToModel_ReturnsValidRawAssetModel() {
        Set<Asset> assetRecords = assetService.getAssetData();
        Set<RawAssetModel> rawAssetModels = assetService.convertToModel();

        assertDoesNotThrow(() -> rawAssetModels);
        assertNotNull(rawAssetModels, "Raw asset models should not be null");
        assertEquals(rawAssetModels.size(), assetRecords.size(), "Set size of the records and models should be equal");

        // For confirming proper conversion
        for (RawAssetModel assetModel : rawAssetModels) {
            System.out.println(assetModel);
        }
    }
}
