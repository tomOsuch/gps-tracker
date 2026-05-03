CREATE TABLE devices (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255)        NOT NULL,
    type        VARCHAR(100)        NOT NULL,
    external_id VARCHAR(255) UNIQUE NOT NULL,
    created_at  TIMESTAMPTZ         NOT NULL DEFAULT now()
);

CREATE TABLE locations (
    id          BIGSERIAL PRIMARY KEY,
    device_id   BIGINT           NOT NULL REFERENCES devices (id),
    latitude    DOUBLE PRECISION NOT NULL,
    longitude   DOUBLE PRECISION NOT NULL,
    recorded_at TIMESTAMPTZ      NOT NULL,
    created_at  TIMESTAMPTZ      NOT NULL DEFAULT now()
);

CREATE INDEX idx_locations_device_recorded
    ON locations (device_id, recorded_at DESC);
