package com.example.data_processing_service.repository.processed;

import com.example.data_processing_service.models.processed.MarketModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketModelRepository extends JpaRepository<MarketModel, String> {
}
