package com.example.data_ingestion_service.services.unit;

import com.example.data_ingestion_service.models.RawMarketModel;
import com.example.data_ingestion_service.services.DatabaseService;
import com.example.data_ingestion_service.services.MarketService;
import com.example.data_ingestion_service.services.dto.EventDTO;
import com.example.data_ingestion_service.services.exceptions.ApiException;
import com.example.data_ingestion_service.services.exceptions.DatabaseException;
import com.example.data_ingestion_service.services.exceptions.OrchestratorException;
import com.example.data_ingestion_service.services.exceptions.ValidationException;
import com.example.data_ingestion_service.services.impl.OrchestratorServiceImpl;
import com.example.data_ingestion_service.services.producer.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.KafkaException;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrchestratorServiceTest {

    @Mock
    private MarketService marketService;

    @Mock
    private DatabaseService databaseService;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private OrchestratorServiceImpl orchestratorService;

    private Set<RawMarketModel> testModels;
    private RawMarketModel testModel;

    @BeforeEach
    void setUp() {
        testModel = new RawMarketModel();
        testModel.setTimestamp(1234567890L);

        testModels = new HashSet<>();
        testModels.add(testModel);
    }

    @Test
    void executeDataPipeline_Success() throws DatabaseException {
        when(marketService.convertToModel()).thenReturn(testModels);
        doNothing().when(databaseService).saveToDatabase(any());
        doNothing().when(kafkaProducer).sendMessage(any());

        assertDoesNotThrow(() -> orchestratorService.executeDataPipeline());

        verify(marketService).convertToModel();
        verify(databaseService).saveToDatabase(testModels);
        verify(kafkaProducer).sendMessage(any(EventDTO.class));
    }

    @Test
    void executeDataPipeline_ApiException() {
        when(marketService.convertToModel()).thenThrow(new ApiException("API Error"));

        OrchestratorException exception = assertThrows(
                OrchestratorException.class,
                () -> orchestratorService.executeDataPipeline()
        );
        assertEquals("Pipeline failed: API error", exception.getMessage());
        assertInstanceOf(ApiException.class, exception.getCause());
    }

    @Test
    void executeDataPipeline_ValidationException() {
        testModel.setTimestamp(null);
        when(marketService.convertToModel()).thenReturn(testModels);

        OrchestratorException exception = assertThrows(OrchestratorException.class, () -> orchestratorService.executeDataPipeline());
        assertInstanceOf(ValidationException.class, exception.getCause());
    }

    @Test
    void saveData_Success() throws DatabaseException {
        doNothing().when(databaseService).saveToDatabase(testModels);

        assertDoesNotThrow(() -> orchestratorService.saveData(testModels));

        verify(databaseService).saveToDatabase(testModels);
    }

    @Test
    void saveData_DatabaseException() throws DatabaseException {
        doThrow(new DataAccessException("Database Error") {}).when(databaseService).saveToDatabase(any());

        DatabaseException exception = assertThrows(DatabaseException.class, () -> orchestratorService.saveData(testModels));
        assertEquals("Failed to save market data", exception.getMessage());
        assertInstanceOf(DataAccessException.class, exception.getCause());
    }

    @Test
    void notifyPipelineCompletion_Success() {
        doNothing().when(kafkaProducer).sendMessage(any());

        assertDoesNotThrow(() -> orchestratorService.notifyPipelineCompletion(1234567890L));

        verify(kafkaProducer).sendMessage(any(EventDTO.class));
    }

    @Test
    void notifyPipelineCompletion_KafkaException() {
        doThrow(new KafkaException("Kafka Error")).when(kafkaProducer).sendMessage(any());

        assertThrows(KafkaException.class, () -> orchestratorService.notifyPipelineCompletion(1234567890L));
    }

    @Test
    void getTimeStamp_NullTimestamp() {
        testModel.setTimestamp(null);
        when(marketService.convertToModel()).thenReturn(testModels);

        OrchestratorException exception = assertThrows(OrchestratorException.class, () -> orchestratorService.executeDataPipeline());
        assertEquals("Validation error in pipeline: Timestamp given is null", exception.getMessage());
    }

    @Test
    void executeDataPipeline_EmptyModelSet() {
        when(marketService.convertToModel()).thenReturn(new HashSet<>());

        assertThrows(NoSuchElementException.class, () -> orchestratorService.executeDataPipeline());
    }
}
