package com.example.data_ingestion_service.services.unit;

import com.example.data_ingestion_service.services.impl.DatabaseServiceImpl;
import com.example.data_ingestion_service.services.mapper.RepositoryMapper;
import com.example.data_ingestion_service.services.producer.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DatabaseServiceTest {

    @Mock
    RepositoryMapper repositoryMapper;

    @Mock
    KafkaProducer kafkaProducer;

    @InjectMocks
    DatabaseServiceImpl databaseService;

    @Test
    void saveToDatabase_DetectsEntityClass_SavesSuccessfully_AndSendsCompletionStatus() {
        verify(repositoryMapper);
    }

    @Test
    void saveToDatabase_DetectsUnknownEntity_ThrowsException() {
        Object unknownEntity = new Object();

    }
}
