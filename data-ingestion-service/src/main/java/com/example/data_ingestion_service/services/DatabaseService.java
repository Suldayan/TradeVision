package com.example.data_ingestion_service.services;

import jakarta.annotation.Nonnull;

import java.util.Set;

public interface DatabaseService {
    <S> void saveToDatabase(@Nonnull Set<S> entities);
}
