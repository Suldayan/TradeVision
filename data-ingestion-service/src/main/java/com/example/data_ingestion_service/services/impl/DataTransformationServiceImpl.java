package com.example.data_ingestion_service.services.impl;

import com.example.data_ingestion_service.models.processed.AssetModel;
import com.example.data_ingestion_service.models.processed.ExchangeModel;
import com.example.data_ingestion_service.models.processed.MarketModel;
import com.example.data_ingestion_service.models.raw.RawAssetModel;
import com.example.data_ingestion_service.models.raw.RawExchangesModel;
import com.example.data_ingestion_service.models.raw.RawMarketModel;
import com.example.data_ingestion_service.repository.RawAssetModelRepository;
import com.example.data_ingestion_service.repository.RawExchangeModelRepository;
import com.example.data_ingestion_service.repository.RawMarketModelRepository;
import com.example.data_ingestion_service.services.DataTransformationService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
    // TODO: configure the processed repositories and replace these with those
    private final RawMarketModelRepository marketModelRepository;
    private final RawExchangeModelRepository exchangeModelRepository;
    private final RawAssetModelRepository assetModelRepository;

    private final DataAggregateServiceImpl aggregateService;

    /*
    * Converts the raw entity model into an exchange model
    * @return a set of exchange models, ensuring there are no duplicates
    * */
    @Override
    public Set<ExchangeModel> rawToEntityExchange() {
        List<RawExchangesModel> cachedExchanges = aggregateService.fetchExchanges();
        return cachedExchanges.stream()
                .map(attribute -> ExchangeModel.builder()
                        .id(attribute.getId())
                        .name(attribute.getName())
                        .percentTotalVolume(attribute.getPercentTotalVolume())
                        .volumeUsd(attribute.getVolumeUsd())
                        .updated(attribute.getUpdated())
                        .markets(null)
                        .build())
                .collect(Collectors.toSet());
    }

    /*
    * Converts a raw asset model into an asset model
    * @return  a set of asset models to ensure there are no duplicates
    * */
    @Override
    public Set<AssetModel> rawToEntityAsset() {
        List<RawAssetModel> cachedAssets = aggregateService.fetchAssets();
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
                        .baseMarkets(null)
                        .quoteMarkets(null)
                        .build())
                .collect(Collectors.toSet());
    }

    /*
    * Creates a map of string and exchange models, indexing by id for easier finding
    * @return a Map<String, ExchangeModel>, containing all ids and their corresponding exchange model
    * */
    @Override
    public Map<String, ExchangeModel> indexExchanges() {
        Set<ExchangeModel> exchangeModels = rawToEntityExchange();
        return exchangeModels.stream()
                .collect(Collectors.toMap(ExchangeModel::getId, Function.identity()));
    }

    /*
     * Creates a map of string and asset models, indexing by id for easier finding
     * @return a Map<String, AssetModel>, containing all ids and their corresponding exchange model
     * */
    @Override
    public Map<String, AssetModel> indexAssets() {
        Set<AssetModel> assetModels = rawToEntityAsset();
        return assetModels.stream()
                .collect(Collectors.toMap(AssetModel::getId, Function.identity()));
    }

    /*
    * Completes all models by connecting each corresponding market, asset and exchange relationship with each other
    * Evicts the cache for the next scheduled fetch via the data aggregate service
    * */
    @CacheEvict(cacheNames = "marketApiResponse, exchangeApiResponse, assetApiResponse")
    @Override
    public void completeMarketAttributes() {
        List<RawMarketModel> filteredMarketModels = aggregateService.collectAndUpdateMarketState();
        Map<String, ExchangeModel> exchanges = indexExchanges();
        Map<String, AssetModel> assets = indexAssets();

        filteredMarketModels
                .forEach(attribute -> {
                    MarketModel marketModel = MarketModel.builder()
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
                            .build();

                    ExchangeModel exchange = exchanges.get(attribute.getExchangeId());
                    if (exchange != null) {
                        exchange.addMarket(marketModel);
                        saveToDatabase(exchange);
                    } else {
                        log.debug("The corresponding exchange model with id: {} does not exist", attribute.getExchangeId());
                    }

                    AssetModel quote = assets.get(attribute.getQuoteId());
                    if (quote != null) {
                        quote.addQuoteMarket(marketModel);
                        saveToDatabase(quote);
                    } else {
                        log.debug("The corresponding quote asset model with id: {} does not exist", attribute.getExchangeId());
                    }

                    AssetModel base = assets.get(attribute.getId());
                    if (base != null) {
                        base.addBaseMarket(marketModel);
                        saveToDatabase(base);
                    } else {
                        log.debug("The corresponding base asset model with id: {} does not exist", attribute.getExchangeId());
                    }

                    saveToDatabase(marketModel);
                });
    }

    /*
     * Generic function for saving to the database
     * @param takes a generic type T, entity, and will detect the class type of that entity and save it to its corresponding repository
     * */
    @Transactional
    @Override
    public <T> void saveToDatabase(@Nonnull T entity) {
        switch (entity) {
            case RawMarketModel rawMarketModel -> {
                marketModelRepository.save(rawMarketModel);
                log.debug("Saving data of type: Market");
            }
            case RawExchangesModel rawExchangesModel -> {
                exchangeModelRepository.save(rawExchangesModel);
                log.debug("Saving data of type: Exchange");
            }
            case RawAssetModel rawAssetModel -> {
                assetModelRepository.save(rawAssetModel);
                log.debug("Saving data of type: Asset");
            }
            default -> log.warn("Info was sent to be saved but was not a recognizable type");
        }
    }
}
