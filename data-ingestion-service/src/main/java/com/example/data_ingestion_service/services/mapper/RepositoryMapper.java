package com.example.data_ingestion_service.services.mapper;

import com.example.data_ingestion_service.models.raw.RawAssetModel;
import com.example.data_ingestion_service.models.raw.RawExchangesModel;
import com.example.data_ingestion_service.models.raw.RawMarketModel;
import com.example.data_ingestion_service.repository.RawAssetModelRepository;
import com.example.data_ingestion_service.repository.RawExchangeModelRepository;
import com.example.data_ingestion_service.repository.RawMarketModelRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RepositoryMapper {
    private final RawExchangeModelRepository exchangeModelRepository;
    private final RawAssetModelRepository assetModelRepository;
    private final RawMarketModelRepository marketModelRepository;
    private Map<Class<?>, CrudRepository<?, ?>> repositoryMap;


    @PostConstruct
    public void createRepositoryMapping() {
        repositoryMap = Map.of(
                RawExchangesModel.class, exchangeModelRepository,
                RawAssetModel.class, assetModelRepository,
                RawMarketModel.class, marketModelRepository
        );
    }

    /*
     * Unchecked warning is suppressed due to personal control over the mapping of repositories
     * */
    @SuppressWarnings("unchecked")
    public <S> CrudRepository<S, ?> getRepository(Class<?> entityClass) {
        return (CrudRepository<S, ?>) repositoryMap.get(entityClass);
    }
}
