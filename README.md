# Construction Workforce HRMS Backend

Backend system for workforce attendance, overtime tracking, and payroll settlement designed for construction industry operations.

Built using Java + Spring Boot with PostgreSQL, Redis caching, Flyway migrations, and production-oriented backend design principles.

---

# Features

## Attendance Management

* Worker clock-in and clock-out APIs
* Real-time active worker tracking
* Attendance history with pagination
* Site-based attendance logging
* Automatic overtime calculation
* 16-hour shift auto-flagging

## Overtime Management

* Monthly overtime summaries
* Tiered overtime payout calculation
* Monthly overtime cap enforcement (60 hours)
* Settlement workflow
* Atomic settlement transactions
* After-commit notification events

## Production Engineering

* Redis graceful degradation
* Optimized Hibernate queries
* Pagination support
* N+1 query prevention using EntityGraph
* Configurable CORS handling
* HikariCP connection pool tuning
* Environment-specific configuration
* Structured API error responses

---

# Tech Stack

* Java 17
* Spring Boot 3
* Spring Security
* Spring Data JPA / Hibernate
* PostgreSQL
* Redis
* Flyway
* Docker
* Maven

---

# Architecture Overview

The system follows a layered backend architecture:

Controller → Service → Repository → PostgreSQL

Redis is used as a high-speed cache layer for active workers currently clocked in at construction sites.

Overtime settlement uses event-driven processing with `@TransactionalEventListener(AFTER_COMMIT)` to ensure notifications are only triggered after successful database commits.

---

# Project Structure

```text
src/main/java/com/example/HRMS/demo

attendance/
worker/
site/
overtime/
cache/
config/
events/
listener/
common/
```

---

# Setup Instructions

## 1. Clone Repository

```bash
git clone <YOUR_GITHUB_REPO_URL>
cd laborforce-hrms
```

---

# 2. Start PostgreSQL + Redis

Using Docker:

```bash
docker compose up -d
```

---

# 3. Configure Database

Update `application-dev.yml` if required:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hrms
    username: postgres
    password: postgres
```

---

# 4. Run Application

Linux / Mac:

```bash
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Application runs on:

```text
http://localhost:8080
```

---

# Redis Configuration

Redis is used only for active attendance tracking.

Features:

* Active worker cache
* TTL safety expiration (16 hours)
* Graceful degradation when Redis is unavailable
* Automatic cache recovery when Redis reconnects

If Redis is offline:

* application still starts
* APIs still work
* only caching is disabled temporarily

---

# Database Migrations

Flyway migrations are located at:

```text
src/main/resources/db/migration
```

Migrations execute automatically during application startup.

---

# Supabase Setup

For staging/production deployment, Supabase PostgreSQL can be used.

Important:

* Use PgBouncer pooled connection URL
* Use port `6543` instead of direct PostgreSQL port `5432`

Example:

```text
jdbc:postgresql://aws-0-ap-south-1.pooler.supabase.com:6543/postgres
```

Reason:

* prevents connection exhaustion
* improves scalability
* works correctly with HikariCP

---

# API Endpoints

## Attendance APIs

### Clock In

```http
POST /api/attendance/clock-in
```

Request:

```json
{
  "workerId": 1,
  "siteId": 1
}
```

---

### Clock Out

```http
POST /api/attendance/clock-out
```

Request:

```json
{
  "workerId": 1
}
```

---

### Active Workers

```http
GET /api/attendance/active
```

Served directly from Redis cache.

---

### Attendance History

```http
GET /api/attendance/log?workerId=1&from=2026-05-01&to=2026-05-31&page=0&size=20
```

Paginated response with metadata.

---

# Overtime APIs

## Monthly Summary

```http
GET /api/overtime/summary/{workerId}?month=2026-05
```

---

## Settle Overtime

```http
POST /api/overtime/settle/{workerId}?month=2026-04
```

Rules:

* current month cannot be settled
* settlement is atomic
* notifications trigger only after successful commit

---

# Business Rules

## Clock-In Rules

* worker must exist
* worker must be active
* site must be active
* no duplicate clock-in allowed
* future timestamps rejected

## Clock-Out Rules

* worker must already be clocked in
* shifts above 16 hours are flagged

## Overtime Rules

* standard shift = 8 hours
* first 2 overtime hours = 1.5x rate
* remaining overtime = 2x rate
* monthly cap = 60 hours

## Settlement Rules

* current month cannot be settled
* settled entries cannot be modified
* settlement is fully transactional

---

# Production Tickets Solved

## LF-201

Implemented configurable CORS handling with Spring Security integration.

## LF-202

Implemented Redis graceful degradation using custom `CacheErrorHandler`.

## LF-203

Fixed N+1 query issue using `@EntityGraph` and added pagination support.

## LF-204

Implemented atomic overtime settlement with `@TransactionalEventListener(AFTER_COMMIT)` notifications.

## LF-205

Optimized HikariCP settings and moved external API calls outside transactional boundaries.

---

# Design Decisions

## Why Redis Only For Active Workers?

Active worker tracking requires extremely fast reads and frequent updates. Historical attendance remains in PostgreSQL for durability and reporting.

## Why AFTER_COMMIT Events?

SMS notifications should only trigger after successful database commit. Prevents false settlement notifications.

## Why Disable Open Session In View?

`spring.jpa.open-in-view=false` prevents accidental lazy-loading during serialization and encourages explicit query optimization.

## Why EntityGraph?

Prevents N+1 query problems when loading attendance history with Worker and Site relationships.

---

# AI Tools Used

* ChatGPT

    * architecture guidance
    * transaction design
    * Redis resilience patterns
    * HikariCP tuning guidance

* GitHub Copilot

    * boilerplate generation
    * DTO scaffolding
    * repository method suggestions

All code was reviewed, modified, and tested manually.

---

# Postman Collection

Postman collection included in repository:

```text
construction-hrms.postman_collection.json
```

---

# Future Improvements

* JWT authentication
* Role-based authorization
* SMS queue retry mechanism
* Kafka/RabbitMQ event streaming
* Audit logging
* Kubernetes deployment
* Monitoring dashboards

---

# Author

Backend Developer Assignment (2026)