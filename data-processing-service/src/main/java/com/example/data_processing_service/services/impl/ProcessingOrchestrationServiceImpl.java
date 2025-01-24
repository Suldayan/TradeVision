package com.example.data_processing_service.services.impl;

import com.example.data_processing_service.services.DataNormalizationService;
import com.example.data_processing_service.services.ProcessingOrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessingOrchestrationServiceImpl implements ProcessingOrchestratorService {
    private final DataNormalizationService dataNormalizationService;

    @Override
    public void startProcessingFlow() {

    }
}
