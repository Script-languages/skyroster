# SkyRoster

## Requirements

- Java 25
- Docker

## Running locally

### 1. Copy environment file (defaults work out of the box)
```bash
cp .env-template .env
```

### 2. Start the application

#### 2.1. Full
```bash
docker compose -f infrastructure/local/docker-compose.yaml --profile full up -d
```

#### 2.2 Backend + its infrastructure (PostgreSQL + Keycloak)
```bash
docker compose -f infrastructure/local/docker-compose.yaml --profile backend up -d
```


#### 2.3 Frontend + its infrastructure (PostgreSQL + Keycloak)
```bash
docker compose -f infrastructure/local/docker-compose.yaml --profile frontend up -d
```

