package com.example.data_ingestion_service.services;

import jakarta.annotation.Nonnull;

import java.util.List;

public interface DatabaseService {
    <S> void saveToDatabase(@Nonnull List<S> entities);
}
