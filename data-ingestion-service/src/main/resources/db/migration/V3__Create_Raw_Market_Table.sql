CREATE TABLE IF NOT EXISTS raw_market_model (
    model_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    base_id VARCHAR(255) NOT NULL,
    rank INTEGER,
    price_quote NUMERIC(18, 6),
    price_usd NUMERIC(18, 2),
    volume_usd_24_hr NUMERIC(18, 2),
    percent_exchange_volume NUMERIC(18, 6),
    trades_count INTEGER,
    updated BIGINT,
    exchange_id VARCHAR(255) NOT NULL,
    quote_id VARCHAR(255) NOT NULL,
    base_symbol VARCHAR(255),
    quote_symbol VARCHAR(255)
);
