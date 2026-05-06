# Supplier Management Module

A production-ready Spring Boot backend for managing suppliers in a Stock Manager system. Built with REST APIs, Kafka event publishing for audit/notifications, and PostgreSQL persistence.

## 🚀 Tech Stack

- **Spring Boot** 3.3.x
- **Java** 17
- **PostgreSQL** 16
- **Apache Kafka** (Confluent 7.6)
- **JPA / Hibernate**
- **Spring Security**
- **Swagger / OpenAPI 3**
- **Maven**
- **Docker & Docker Compose**

## 📁 Project Structure

```
src/main/java/com/brightcore/suppliermanagement/
├── config/           # Kafka topic & Security configuration
├── controller/       # REST Controllers
├── dto/              # Request/Response DTOs
├── entity/           # JPA Entities
├── event/            # Kafka event models
├── exception/        # Global exception handling
├── kafka/            # Kafka Producer & Consumer
├── repository/       # Spring Data JPA repositories
└── service/          # Business logic (interface + impl)
```

## 🔌 API Endpoints

| Method | Path                              | Description       | Success Code |
|--------|-----------------------------------|-------------------|--------------|
| POST   | `/api/v1/suppliers/add`           | Create supplier   | 201 Created  |
| PUT    | `/api/v1/suppliers/update/{id}`   | Update supplier   | 200 OK       |
| GET    | `/api/v1/suppliers/{id}`          | Get supplier      | 200 OK       |
| DELETE | `/api/v1/suppliers/delete/{id}`   | Delete supplier   | 200 OK       |

Every operation publishes a `SupplierEvent` to the `supplier-events` Kafka topic for audit and downstream notifications.

### Event Types
- `SUPPLIER_CREATED`
- `SUPPLIER_UPDATED`
- `SUPPLIER_RETRIEVED`
- `SUPPLIER_DELETED`

## 🐳 Run with Docker (recommended)

Make sure Docker and Docker Compose are installed, then from the project root:

```bash
docker compose up --build
```

This spins up:
- Spring Boot application → `localhost:8080`
- PostgreSQL database → `localhost:5432`
- Apache Kafka → `localhost:9092`
- Zookeeper → `localhost:2181`

**Access:**
- App base URL: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

**Stop all services:**
```bash
docker compose down
```

**Stop and wipe database volume:**
```bash
docker compose down -v
```

## 💻 Run Locally (without Docker)

### Prerequisites
- Java 17+
- Maven 3.9+
- PostgreSQL running on `localhost:5432` with database `supplierdb` (user/password: `postgres`/`postgres`)
- Kafka running on `localhost:9092`

### Steps

1. Create the PostgreSQL database:
   ```sql
   CREATE DATABASE supplierdb;
   ```

2. Start Kafka + Zookeeper (quickest via Docker):
   ```bash
   docker compose up -d zookeeper kafka postgres
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

   Or from IntelliJ: right-click `SupplierManagementApplication` → **Run**.

## 🧪 Sample Requests

### Create Supplier
```bash
curl -X POST http://localhost:8080/api/v1/suppliers/add \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Acme Supplies",
    "email": "acme@example.com",
    "phone": "+250788000000",
    "address": "Kigali, Rwanda",
    "contactPerson": "Jane Doe"
  }'
```

### Update Supplier
```bash
curl -X PUT http://localhost:8080/api/v1/suppliers/update/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Acme Supplies Ltd",
    "email": "acme@example.com",
    "phone": "+250788111222",
    "address": "KG 123 St, Kigali",
    "contactPerson": "Jane Doe"
  }'
```

### Get Supplier
```bash
curl http://localhost:8080/api/v1/suppliers/1
```

### Delete Supplier
```bash
curl -X DELETE http://localhost:8080/api/v1/suppliers/delete/1
```

## 📬 Verifying Kafka Events

When you hit any endpoint, check the app logs — the consumer prints audit entries:

```
📥 [AUDIT] Received event: SUPPLIER_CREATED | Supplier: Acme Supplies | At: 2026-05-07T06:00:00
```

To inspect messages directly inside the Kafka container:

```bash
docker exec -it supplier-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic supplier-events \
  --from-beginning
```

## 🏗️ Architecture

```
        HTTP Request
             │
             ▼
    ┌─────────────────┐
    │   Controller    │  ← validation, HTTP status codes
    └────────┬────────┘
             ▼
    ┌─────────────────┐
    │     Service     │  ← business logic, transactions
    └────┬────────┬───┘
         │        │
         ▼        ▼
  ┌───────────┐  ┌──────────────┐
  │ Repository│  │ KafkaProducer│
  └─────┬─────┘  └──────┬───────┘
        ▼               ▼
  ┌──────────┐   ┌─────────────┐
  │PostgreSQL│   │   Kafka     │
  └──────────┘   │ (supplier-  │
                 │  events)    │
                 └──────┬──────┘
                        ▼
                 ┌─────────────┐
                 │  Consumer   │  ← audit logging
                 └─────────────┘
```

## 🔐 Security

Spring Security is configured with:
- CSRF disabled (stateless REST API)
- Stateless session management
- Public access to supplier endpoints and Swagger for evaluation purposes

> For production, JWT-based authentication should be added and endpoints locked down by role.

## ⚙️ Configuration

All configuration is externalized via environment variables (see `application.properties`):

| Variable                   | Default              | Description              |
|----------------------------|----------------------|--------------------------|
| `DB_HOST`                  | `localhost`          | PostgreSQL host          |
| `DB_PORT`                  | `5432`               | PostgreSQL port          |
| `DB_NAME`                  | `supplierdb`         | Database name            |
| `DB_USER`                  | `postgres`           | Database user            |
| `DB_PASSWORD`              | `postgres`           | Database password        |
| `KAFKA_BOOTSTRAP_SERVERS`  | `localhost:9092`     | Kafka bootstrap address  |

## ✅ Features Implemented

- [x] 4 REST endpoints with proper HTTP verbs and status codes
- [x] Kafka event publishing for every operation
- [x] Kafka consumer for audit logging (end-to-end verification)
- [x] PostgreSQL persistence via JPA/Hibernate
- [x] Layered architecture (Controller → Service → Repository)
- [x] DTO pattern with nested request/response classes
- [x] Input validation with Jakarta Validation annotations
- [x] Global exception handling with consistent error responses
- [x] Spring Security configuration
- [x] Swagger/OpenAPI 3 documentation
- [x] Multi-stage Dockerfile (optimized image size)
- [x] docker-compose.yml with all services, healthchecks, and networking
- [x] Environment-based configuration
