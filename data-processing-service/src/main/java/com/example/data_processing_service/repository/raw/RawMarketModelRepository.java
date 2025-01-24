package com.example.data_processing_service.repository.raw;

import com.example.data_processing_service.models.raw.RawMarketModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RawMarketModelRepository extends JpaRepository<RawMarketModel, String> {
    Set<RawMarketModel> findAllByTimestamp(Long timestamp);
}
