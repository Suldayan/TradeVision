package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.models.processed.AssetModel;
import com.example.data_ingestion_service.models.processed.ExchangeModel;
import com.example.data_ingestion_service.models.processed.MarketModel;
import com.example.data_ingestion_service.models.raw.RawAssetModel;
import com.example.data_ingestion_service.models.raw.RawExchangesModel;
import com.example.data_ingestion_service.models.raw.RawMarketModel;
import com.example.data_ingestion_service.repository.AssetModelRepository;
import com.example.data_ingestion_service.repository.ExchangeModelRepository;
import com.example.data_ingestion_service.repository.MarketModelRepository;
import com.example.data_ingestion_service.services.DataAggregateService;
import com.example.data_ingestion_service.services.DataTransformationService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
* Transforms all incoming data from the data aggregate service and fully completes them to be saved onto the database (postgres)
* @param takes a Market, Exchange and Asset Model Repository for saving to the database after all transformations have been applied and takes a DataAggregateServiceImpl to grab the data
* Every transformation involves converting each model from its raw form to its filtered form, then finally filling the attributes with their respective relationships
* The transformation flow is as follows: gather data -> convert (raw to processed) -> fill -> save
* */

@Service
@Slf4j
@RequiredArgsConstructor
public class DataTransformationServiceImpl implements DataTransformationService {
    private final MarketModelRepository marketModelRepository;
    private final ExchangeModelRepository exchangeModelRepository;
    private final AssetModelRepository assetModelRepository;

    private final DataAggregateService aggregateService;

    // TODO: this conversion seems not to be needed anymore as we're removing the circular relationship
    @Override
    public Set<ExchangeModel> rawToEntityExchange() {
        Set<RawExchangesModel> cachedExchanges = aggregateService.exchangeIdsToModels();
        return cachedExchanges.stream()
                .map(attribute -> ExchangeModel.builder()
                        .id(attribute.getId())
                        .name(attribute.getName())
                        .percentTotalVolume(attribute.getPercentTotalVolume())
                        .volumeUsd(attribute.getVolumeUsd())
                        .updated(attribute.getUpdated())
                        .build())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<AssetModel> rawToEntityAsset() {
        Set<RawAssetModel> cachedAssets = aggregateService.assetIdsToModels();
        return cachedAssets.stream()
                .map(attribute -> AssetModel.builder()
                        .id(attribute.getId())
                        .symbol(attribute.getSymbol())
                        .name(attribute.getName())
                        .supply(attribute.getSupply())
                        .maxSupply(attribute.getMaxSupply())
                        .marketCapUsd(attribute.getMarketCapUsd())
                        .volumeUsd24Hr(attribute.getVolumeUsd24Hr())
                        .priceUsd(attribute.getPriceUsd())
                        .changePercent24Hr(attribute.getChangePercent24Hr())
                        .vwap24Hr(attribute.getVwap24Hr())
                        .build())
                .collect(Collectors.toSet());
    }

    @Override
    public Map<String, ExchangeModel> indexExchangesById() {
        Set<ExchangeModel> exchangeModels = rawToEntityExchange();
        return exchangeModels.stream()
                .collect(Collectors.toMap(ExchangeModel::getId, Function.identity()));
    }

    @Override
    public Map<String, AssetModel> indexAssetsById() {
        Set<AssetModel> assetModels = rawToEntityAsset();
        return assetModels.stream()
                .collect(Collectors.toMap(AssetModel::getId, Function.identity()));
    }

    /*
    * Completes market models by completing the exchange and asset relationship towards the market
    * Evicts the cache for the next scheduled fetch via the data aggregate service
    * */
    @CacheEvict(cacheNames = "marketApiResponse, exchangeApiResponse, assetApiResponse")
    @Override
    public void completeMarketAttributes() {
        List<RawMarketModel> filteredMarketModels = aggregateService.collectAndUpdateMarketState();
        Map<String, ExchangeModel> exchanges = indexExchangesById();
        Map<String, AssetModel> assets = indexAssetsById();

        List<MarketModel> marketModels = filteredMarketModels
                .stream()
                .map(attribute -> MarketModel.builder()
                        .id(attribute.getId())
                        .exchange(
                                (ExchangeModel) exchanges.entrySet()
                                        .stream()
                                        .filter(exchangeModel -> Objects.equals(attribute.getExchangeId(), exchangeModel.getKey()))
                                        .findFirst()
                                        .orElseGet(() -> {
                                            log.debug("The corresponding exchange model with id: {} does not exist", attribute.getExchangeId());
                                            return null;
                                        })
                        )
                        .baseAsset(
                                (AssetModel) assets.entrySet()
                                        .stream()
                                        .filter(baseModel -> Objects.equals(attribute.getBaseSymbol(), baseModel.getKey()))
                                        .findFirst()
                                        .orElseGet(() -> {
                                            log.debug("The corresponding base asset model with id: {} does not exist", attribute.getExchangeId());
                                            return null;
                                        })
                        )
                        .quoteAsset(
                                (AssetModel) assets.entrySet()
                                        .stream()
                                        .filter(quoteModel -> Objects.equals(attribute.getBaseSymbol(), quoteModel.getKey()))
                                        .findFirst()
                                        .orElseGet(() -> {
                                            log.debug("The corresponding quote asset model with id: {} does not exist", attribute.getExchangeId());
                                            return null;
                                        })
                        )
                        .baseSymbol(attribute.getBaseSymbol())
                        .quoteSymbol(attribute.getQuoteSymbol())
                        .priceQuote(attribute.getPriceQuote())
                        .priceUsd(attribute.getPriceUsd())
                        .volumeUsd24Hr(attribute.getVolumeUsd24Hr())
                        .percentExchangeVolume(attribute.getPercentExchangeVolume())
                        .tradesCount(attribute.getTradesCount())
                        .updated(attribute.getUpdated())
                        .build())
                .toList();
        saveToDatabase(marketModels);
    }

    /*
     * Generic function for saving to the database
     * @param takes a generic type T, entity, and will detect the class type of that entity and save it to its corresponding repository
     * */
    @Transactional
    @Override
    public <S> void saveToDatabase(@Nonnull List<S> entities) {
        if (entities.isEmpty()) {
            log.warn("The list of entities has been passed but is empty");
            return;
        }
        // Grab the first element to find out what the data type is
        S firstElement = entities.getFirst();
        switch (firstElement) {
            case MarketModel ignored -> {
                @SuppressWarnings(value = "unchecked")
                List<MarketModel> marketModels = (List<MarketModel>) entities;
                marketModelRepository.saveAll(marketModels);
                log.debug("Saving data of type: Market, with size: {}", entities.size());
            }
            case ExchangeModel ignored -> {
                @SuppressWarnings(value = "unchecked")
                List<ExchangeModel> exchangeModels = (List<ExchangeModel>) entities;
                exchangeModelRepository.saveAll(exchangeModels);
                log.debug("Saving data of type: Exchange, with size: {}", entities.size());
            }
            case AssetModel ignored -> {
                @SuppressWarnings(value = "unchecked")
                List<AssetModel> assetModels = (List<AssetModel>) entities;
                assetModelRepository.saveAll(assetModels);
                log.debug("Saving data of type: Asset, with size: {}", entities.size());
            }
            default -> log.warn("Info was sent to be saved but was not a recognizable type");
        }
    }
}
