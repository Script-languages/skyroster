CREATE TABLE operational_bases (
    id        UUID PRIMARY KEY,
    icao_code VARCHAR(4)   UNIQUE NOT NULL,
    name      VARCHAR(100) NOT NULL
);

CREATE TABLE aircraft_types (
    id        UUID PRIMARY KEY,
    icao_code VARCHAR(4)   UNIQUE NOT NULL,
    name      VARCHAR(100) NOT NULL
);

CREATE TABLE aircraft (
    id                  UUID PRIMARY KEY,
    registration_number VARCHAR(15)  UNIQUE NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    aircraft_type_id    UUID         NOT NULL REFERENCES aircraft_types(id),
    operational_base_id UUID         NOT NULL REFERENCES operational_bases(id)
);

-- Seed: operational bases
INSERT INTO operational_bases (id, icao_code, name) VALUES
    ('a0000000-0000-0000-0000-000000000001', 'EPWA', 'Warsaw Chopin'),
    ('a0000000-0000-0000-0000-000000000002', 'EPKK', 'Krakow Balice'),
    ('a0000000-0000-0000-0000-000000000003', 'EPGD', 'Gdansk Lech Walesa');

-- Seed: aircraft types
INSERT INTO aircraft_types (id, icao_code, name) VALUES
    ('b0000000-0000-0000-0000-000000000001', 'B738', 'Boeing 737-800'),
    ('b0000000-0000-0000-0000-000000000002', 'A320', 'Airbus A320'),
    ('b0000000-0000-0000-0000-000000000003', 'AT76', 'ATR 72-600');
