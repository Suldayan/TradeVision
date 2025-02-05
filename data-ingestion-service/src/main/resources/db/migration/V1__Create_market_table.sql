CREATE TABLE IF NOT EXISTS raw_markets (
    model_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    exchange_id VARCHAR(255) NOT NULL,
    rank INTEGER NOT NULL CHECK (rank > 0),
    base_symbol VARCHAR(10) NOT NULL CHECK (length(base_symbol) >= 1),
    base_id VARCHAR(30) NOT NULL CHECK (length(base_id) >= 1),
    quote_symbol VARCHAR(10) NOT NULL CHECK (length(quote_symbol) >= 1),
    quote_id VARCHAR(20) NOT NULL CHECK (length(quote_id) >= 1),
    price_quote DECIMAL(30,8) NOT NULL CHECK (price_quote >= 0),
    price_usd DECIMAL(30,8) NOT NULL CHECK (price_usd >= 0),
    volume_usd_24hr DECIMAL(30,2) NOT NULL CHECK (volume_usd_24hr >= 0),
    percent_exchange_volume DECIMAL(10,4) NOT NULL CHECK (percent_exchange_volume >= 0),
    trades_count_24hr INTEGER CHECK (trades_count_24hr >= 0),
    updated BIGINT NOT NULL CHECK (updated >= 0),
    timestamp BIGINT CHECK (timestamp >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_timestamp ON raw_markets(timestamp);

COMMENT ON TABLE raw_markets IS 'Stores raw market data from exchanges';

COMMENT ON COLUMN raw_markets.model_id IS 'Unique identifier for the market record';
COMMENT ON COLUMN raw_markets.exchange_id IS 'Identifier of the exchange where the market exists';
COMMENT ON COLUMN raw_markets.base_symbol IS 'Trading symbol of the base asset';
COMMENT ON COLUMN raw_markets.base_id IS 'Identifier of the base asset';
COMMENT ON COLUMN raw_markets.quote_symbol IS 'Trading symbol of the quote asset';
COMMENT ON COLUMN raw_markets.quote_id IS 'Identifier of the quote asset';
COMMENT ON COLUMN raw_markets.price_quote IS 'Price in quote currency';
COMMENT ON COLUMN raw_markets.price_usd IS 'Price in USD';
COMMENT ON COLUMN raw_markets.volume_usd_24hr IS '24-hour trading volume in USD';
COMMENT ON COLUMN raw_markets.percent_exchange_volume IS 'Percentage of exchange volume';
COMMENT ON COLUMN raw_markets.trades_count_24hr IS 'Number of trades in the last 24 hours';
COMMENT ON COLUMN raw_markets.updated IS 'Last update timestamp of the market data';
COMMENT ON COLUMN raw_markets.timestamp IS 'Timestamp of when the market data was recorded';