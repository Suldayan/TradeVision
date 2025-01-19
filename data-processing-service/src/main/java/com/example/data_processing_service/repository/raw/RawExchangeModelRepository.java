package com.example.data_processing_service.repository.raw;

import com.example.data_processing_service.models.raw.RawExchangesModel;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface RawExchangeModelRepository extends CrudRepository<RawExchangesModel, String> {
    Set<RawExchangesModel> findAllByTimestamp(Long timestamp);
}
