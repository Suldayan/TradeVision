CREATE TABLE IF NOT EXISTS raw_assets (
    model_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id VARCHAR(255) NOT NULL,
    rank INTEGER NOT NULL,
    symbol VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    supply NUMERIC(18, 6) NOT NULL
    maxSupply NUMERIC(18, 6) NOT NULL,
    marketCapUsd24Hr NUMERIC(18, 6) NOT NULL,
    volumeUsd24Hr NUMERIC(18, 6) NOT NULL,
    priceUsd NUMERIC(18, 6) NOT NULL,
    vwap24Hr NUMERIC(18, 6) NOT NULL,
    explorer TEXT NOT NULL
)