CREATE TABLE IF NOT EXISTS raw_market_data (
    id SERIAL PRIMARY KEY,
    exchangeId VARCHAR(100) NOT NULL,
    rank INT NOT NULL,
    baseId VARCHAR(100) NOT NULL,
    quoteId VARCHAR(100) NOT NULL,
    priceQuote NUMERIC NOT NULL,
    priceUsd NUMERIC NOT NULL,
    volumeUsd24Hr NUMERIC NOT NULL,
    percentExchangeVolume NUMERIC NOT NULL,
    tradesCount24Hr BIGINT NOT NULL,
    updated BIGINT NOT NULL
);

