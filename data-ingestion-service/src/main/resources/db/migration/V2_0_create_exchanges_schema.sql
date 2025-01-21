CREATE TABLE IF NOT EXISTS raw_exchanges (
    model_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    exchange_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    rank INTEGER NOT NULL CHECK (rank > 0),
    percent_total_volume DECIMAL(10,4),
    volume_usd DECIMAL(30,2),
    trading_pairs INTEGER NOT NULL CHECK (trading_pairs >= 0),
    socket BOOLEAN NOT NULL,
    exchange_url VARCHAR(2048) NOT NULL,
    updated BIGINT NOT NULL CHECK (updated >= 0),
    timestamp BIGINT NOT NULL CHECK (timestamp >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_timestamp ON raw_exchanges(timestamp);

COMMENT ON TABLE raw_exchanges IS 'Stores raw exchange data from external sources';

COMMENT ON COLUMN raw_exchanges.model_id IS 'Unique identifier for the exchange record';
COMMENT ON COLUMN raw_exchanges.exchange_id IS 'External identifier for the exchange';
COMMENT ON COLUMN raw_exchanges.percent_total_volume IS 'Percentage of total market volume';
COMMENT ON COLUMN raw_exchanges.volume_usd IS 'Trading volume in USD';
COMMENT ON COLUMN raw_exchanges.trading_pairs IS 'Number of trading pairs available on the exchange';
COMMENT ON COLUMN raw_exchanges.socket IS 'Whether the exchange supports websocket connections';
COMMENT ON COLUMN raw_exchanges.updated IS 'Last update timestamp from the source';
COMMENT ON COLUMN raw_exchanges.timestamp IS 'Timestamp when the data was collected';