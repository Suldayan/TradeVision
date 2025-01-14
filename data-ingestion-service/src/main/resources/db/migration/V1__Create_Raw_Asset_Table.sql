CREATE TABLE raw_asset_data (
    id VARCHAR(50),
    symbol VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    supply DECIMAL(30, 8) NOT NULL,
    maxSupply DECIMAL(60, 8) NOT NULL,
    marketCapUsd DECIMAL(10, 8) NOT NULL,
    volumeUsd24Hr DECIMAL(10, 8) NOT NULL,
    priceUsd DECIMAL(10, 8) NOT NULL,
    changePercent24Hr DECIMAL(10, 8) NOT NULL,
    vwap24Hr DECIMAL(10, 8) NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
);