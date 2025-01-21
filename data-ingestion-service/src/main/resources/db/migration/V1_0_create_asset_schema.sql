CREATE TABLE IF NOT EXISTS raw_assets (
    model_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id VARCHAR(255) NOT NULL,
    rank INTEGER NOT NULL CHECK (rank > 0),
    symbol VARCHAR(10) NOT NULL CHECK (length(symbol) >= 1),
    name VARCHAR(100) NOT NULL CHECK (length(name) >= 1),
    supply DECIMAL(30,8) NOT NULL CHECK (supply >= 0),
    max_supply DECIMAL(30,8) CHECK (max_supply >= 0),
    market_cap_usd DECIMAL(30,2) NOT NULL CHECK (market_cap_usd >= 0),
    volume_usd_24hr DECIMAL(30,2) NOT NULL CHECK (volume_usd_24hr >= 0),
    price_usd DECIMAL(30,8) NOT NULL CHECK (price_usd >= 0),
    change_percent_24hr DECIMAL(10,2) NOT NULL,
    vwap_24hr DECIMAL(30,8) NOT NULL CHECK (vwap_24hr >= 0),
    explorer VARCHAR(2048),
    timestamp BIGINT CHECK (timestamp >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_timestamp ON raw_assets(timestamp);

COMMENT ON TABLE raw_assets IS 'Stores raw asset data from external sources';

COMMENT ON COLUMN raw_assets.model_id IS 'Unique identifier for the asset record';
COMMENT ON COLUMN raw_assets.id IS 'External identifier for the asset';
COMMENT ON COLUMN raw_assets.symbol IS 'Trading symbol of the asset';
COMMENT ON COLUMN raw_assets.market_cap_usd IS 'Market capitalization in USD';
COMMENT ON COLUMN raw_assets.volume_usd_24hr IS '24-hour trading volume in USD';
COMMENT ON COLUMN raw_assets.vwap_24hr IS 'Volume-weighted average price over 24 hours';