package com.example.data_processing_service.repository.processed;

import com.example.data_processing_service.models.processed.ExchangesModel;
import org.springframework.data.repository.CrudRepository;

public interface ExchangeModelRepository extends CrudRepository<ExchangesModel, String> {
}
