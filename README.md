# Comparo

A microservices-based product comparison platform built with Java 21/Spring Boot and Angular. Users can browse products, compare specifications side-by-side, read and write reviews, and find offers from different shops. Elasticsearch powers full-text search with faceted filtering, and Apache Kafka drives asynchronous event propagation between services.

## Architecture Overview

The platform follows a microservices architecture with an API Gateway as the single entry point. All backend services communicate asynchronously through Kafka events, while the Angular SPA communicates with the backend exclusively through the gateway's REST API.

```
┌──────────────┐
│  Angular SPA  │  (comparo-web, port 4200)
└──────┬───────┘
       │ HTTP
┌──────▼───────┐
│  API Gateway  │  (Spring Cloud Gateway, port 80)
└──────┬───────┘
       │ Routes to:
       ├── User Service       (port 8001)
       ├── Catalog Service    (port 8003)
       ├── Search Service     (port 8005)
       ├── Review Service     (port 8009)
       ├── Offer Service      (port 8011)
       └── Admin Service      (port 8015)

Infrastructure:
  ├── Keycloak        (port 8080)  — OAuth2/OIDC identity provider
  ├── Kafka           (port 9092)  — Event streaming (KRaft mode)
  ├── PostgreSQL x4   — User, Offer, Admin, Review databases
  ├── MongoDB         — Catalog database
  ├── Elasticsearch   — Search index
  ├── Redis           — Rate limiting & caching
  └── MinIO           — S3-compatible image storage
```

## Tech Stack

### Backend
- **Language**: Java 21
- **Framework**: Spring Boot 4.0.3, Spring Cloud 2025.1.1
- **Build**: Maven 3.9.6 (per-service `pom.xml`, no parent POM)
- **Auth**: Keycloak 26.0.8 (OAuth2/JWT, realm: `comparo`)
- **Databases**: PostgreSQL 16, MongoDB 7.0, Elasticsearch 9.2.5
- **Messaging**: Apache Kafka 3.7.0 (KRaft mode)
- **Cache**: Redis 7.2
- **Storage**: MinIO (S3-compatible)
- **Mapping**: MapStruct 1.5.5, Lombok
- **Migrations**: Liquibase (XML changelogs for PostgreSQL services)
- **Containers**: Multi-stage Dockerfiles (Maven build → eclipse-temurin:21-jre-alpine)

### Frontend
- **Framework**: Angular 19 (standalone components)
- **UI**: Angular Material, custom SCSS
- **Auth**: Keycloak JS adapter
- **State**: Angular Signals
- **Build**: Angular CLI / Vite

## Services

| Service | Port | Database | Description |
|---------|------|----------|-------------|
| `api-gateway` | 80 | Redis | Spring Cloud Gateway, routes `/api/v1/*`, rate limiting |
| `user-service` | 8001 | PostgreSQL | User profiles, Keycloak registration event consumer |
| `catalog-service` | 8003 | MongoDB | Products, categories, comparisons, image uploads via MinIO |
| `search-service` | 8005 | Elasticsearch | Full-text search, aggregations, consumes Kafka events |
| `review-service` | 8009 | PostgreSQL | Product reviews with moderation workflow |
| `offer-service` | 8011 | PostgreSQL | Shop offers, price tracking |
| `admin-service` | 8015 | PostgreSQL | User management (ban/unban via Keycloak), platform stats |
| `keycloak-plugin` | — | — | Keycloak SPI: emits Kafka events on user registration |

## Kafka Event Flow

```
Keycloak ──(user.registered)──► User Service
User Service ──(user.updated)──► Search Service, Admin Service
Catalog Service ──(product-events)──► Search Service
Offer Service ──(offer-updates)──► Search Service
Review Service ──(product-ratings)──► Search Service
Offer Service ──(offer-ingested-topic)──► internal processing
```

## Getting Started

### Prerequisites
- Docker & Docker Compose
- Java 21 (for local development)
- Maven 3.9+ (for local development)
- Node.js 18+ and Angular CLI (for frontend development)

### Quick Start

```bash
# 1. Build the Keycloak plugin (required before starting)
cd keycloak-plugin
mvn clean package
cd ..

# 2. Start all infrastructure and services
docker compose up -d

# 3. Start the frontend (in a separate terminal)
cd ../comparo-web
npm install
ng serve
```

The application will be available at:
- **Frontend**: http://localhost:4200
- **API Gateway**: http://localhost:80
- **Keycloak Admin**: http://localhost:8080 (admin/admin)
- **MinIO Console**: http://localhost:9001 (comparo_minio/secure_minio_password)

### Infrastructure Only (for local service development)

```bash
docker compose up -d user-db offer-db catalog-db admin-db review-db elasticsearch redis kafka keycloak minio
```

### Building a Single Service

```bash
cd <service-directory>
./mvnw clean package -DskipTests
```

## Project Structure

```
comparo/
├── api-gateway/          # Spring Cloud Gateway
├── user-service/         # User profile management
├── catalog-service/      # Product catalog (MongoDB)
├── search-service/       # Elasticsearch-powered search
├── review-service/       # Product reviews
├── offer-service/        # Shop offers
├── admin-service/        # Admin operations
├── keycloak-plugin/      # Keycloak event listener SPI
├── docker-compose.yaml   # Full infrastructure definition
└── CLAUDE.md             # Development conventions

comparo-web/              # Angular 19 SPA (separate repository root)
├── src/app/
│   ├── core/             # Auth, guards, interceptors
│   ├── features/         # Feature modules (home, search, product, etc.)
│   └── shared/           # Services, models, components
└── src/environments/     # Environment configuration
```

### Per-Service Structure

```
<service>/src/main/java/com/pr0f1t/comparo/<service>/
├── controller/     # REST endpoints
├── service/        # Interface + *ServiceImpl
├── repository/     # Spring Data interfaces
├── entity/         # JPA/Mongo/ES domain objects
├── dto/            # Request/Response DTOs, event/ for Kafka events
├── mapper/         # MapStruct interfaces
├── exception/      # Domain exceptions + GlobalExceptionHandler
├── config/         # SecurityConfig, Kafka*Config, etc.
├── kafka/          # producer/ and consumer/
└── bootstrap/      # Data seeders (catalog-service, offer-service)
```

## Key Features

- **Product Search**: Full-text search with category filters, price range, attribute facets, and sorting
- **Product Comparison**: Side-by-side specification comparison for up to 4 products
- **Reviews**: User reviews with star ratings and admin moderation workflow
- **Offers**: Multiple shop offers per product with availability tracking
- **Trending Products**: Redis-based view tracking for trending product display
- **Saved Lists**: User-created product lists with localStorage persistence
- **Favorites**: Quick product bookmarking
- **Admin Panel**: User management (ban/unban), review moderation, product/offer management
- **Image Storage**: Product images uploaded to MinIO with automatic seeding

## API Routes

All API endpoints are prefixed with `/api/v1/` and routed through the API Gateway:

| Route Pattern | Service |
|---------------|---------|
| `/api/v1/users/**` | user-service |
| `/api/v1/catalog/**` | catalog-service |
| `/api/v1/search/**` | search-service |
| `/api/v1/offers/**` | offer-service |
| `/api/v1/reviews/**` | review-service |
| `/api/v1/admin/**` | admin-service |
| `/storage/**` | MinIO (proxied) |
