# SkyRoster

## Requirements

- Java 25
- Docker

## Running locally

```bash
# 1. Copy environment file (defaults work out of the box)
cp .env-template .env

# 2. Start infrastructure (PostgreSQL + Keycloak)
docker compose -f infrastructure/local/docker-compose.yaml --profile backend up -d

# 3. Backend
cd skyroster-backend
./mvnw spring-boot:run
```

