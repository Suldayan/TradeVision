package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.models.processed.MarketModel;
import com.example.data_processing_service.repository.processed.MarketModelRepository;
import com.example.data_processing_service.services.DataPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataPersistenceServiceImpl implements DataPersistenceService {
    private final MarketModelRepository marketModelRepository;

    @Override
    public void saveToDatabase(Set<MarketModel> marketModels) {

    }
}
