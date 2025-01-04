package com.example.data_processing_service.repository.raw;

import com.example.data_processing_service.models.raw.RawExchangesModel;
import org.springframework.data.repository.CrudRepository;

public interface RawExchangeModelRepository extends CrudRepository<RawExchangesModel, String> {
}
