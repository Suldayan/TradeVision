package com.example.data_processing_service.repository.processed;

import com.example.data_processing_service.models.processed.MarketModel;
import org.springframework.data.repository.CrudRepository;

public interface MarketModelRepository extends CrudRepository<MarketModel, String> {
}
