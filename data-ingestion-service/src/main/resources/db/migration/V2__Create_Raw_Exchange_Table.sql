CREATE TABLE IF NOT EXISTS raw_exchanges (
    model_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    exchange_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    rank INTEGER,
    percent_total_volume NUMERIC(18, 6),
    volume_usd NUMERIC(18, 2),
    trading_pairs INTEGER,
    socket BOOLEAN,
    exchange_url TEXT,
    updated BIGINT
);
