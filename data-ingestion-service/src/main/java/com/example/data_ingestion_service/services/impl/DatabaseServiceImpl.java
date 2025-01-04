package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.services.DatabaseService;
import com.example.data_ingestion_service.services.mapper.RepositoryMapper;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatabaseServiceImpl implements DatabaseService {
    private final RepositoryMapper repositoryMapper;

    @Transactional
    @Override
    public <S> void saveToDatabase(@Nonnull List<S> entities) {
        if (entities.isEmpty()) {
            log.warn("The list of entities for saving has been passed but is empty");
        }
        Class<?> entityClass = entities.getFirst().getClass();
        CrudRepository<S, ?> repository = repositoryMapper.getRepository(entityClass);
        if (repository != null) {
            repository.saveAll(entities);
            log.debug("Saving entities of type: {} with list size: {}", entities.getClass().getSimpleName(), entities.size());
        }
        log.warn("The class of the given entity does not exist within the repository mapping");
    }
}
