# Labor Workforce HRMS Backend

Backend system for workforce attendance, overtime tracking, payroll settlement, and active workforce monitoring designed for labor-intensive construction and field operations.

This project was completed as part of a Java Backend Developer hiring assignment and focuses on production-grade backend engineering practices including transactional integrity, ORM optimization, Redis resilience, caching strategies, connection pool tuning, and business-rule enforcement.

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

# Project Origin

This solution was built from a Spring Initializr starter project and was not forked from an existing HRMS repository.

Reason:

The assignment focused on backend architecture and business-rule implementation, so I chose to design the domain model and workflows from scratch rather than adapt an existing codebase.

# Assignment Coverage

## Workforce Attendance & Overtime Engine

### Schema Design

Implemented:

- Worker
- Site
- AttendanceLog
- OvertimeEntry

### Attendance APIs

- POST /api/attendance/clock-in
- POST /api/attendance/clock-out
- GET /api/attendance/active
- GET /api/attendance/log

### Overtime APIs

- GET /api/overtime/summary/{workerId}
- POST /api/overtime/settle/{workerId}

### Redis Features

- Active worker cache
- Redis-only active worker endpoint
- 16-hour TTL safety net
- Cache invalidation support
- Graceful degradation when Redis is unavailable

### Business Rules

- No duplicate clock-in
- Worker/site active validation
- Future timestamp validation
- 16-hour shift flagging
- 60-hour monthly overtime cap
- Atomic settlement processing

---

# Architecture Overview

The system follows a layered backend architecture:

Controller → Service → Repository → PostgreSQL

Redis is used as a high-speed cache layer for active workers currently clocked in at labor sites.

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

---

# Curl Examples

```bash
curl -X POST http://localhost:8080/api/attendance/clock-in \
-H "Content-Type: application/json" \
-d '{"workerId":1,"siteId":1}'
```

```bash
curl -X POST http://localhost:8080/api/attendance/clock-out \
-H "Content-Type: application/json" \
-d '{"workerId":1}'
```

```bash
curl http://localhost:8080/api/attendance/active
```

```bash
curl "http://localhost:8080/api/attendance/log?workerId=1&from=2026-05-01&to=2026-05-31&page=0&size=20"
```

```bash
curl "http://localhost:8080/api/overtime/summary/1?month=2026-05"
```

```bash
curl -X POST "http://localhost:8080/api/overtime/settle/1?month=2026-04"
```
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

# Testing Performed

Verified scenarios:

- Successful worker clock-in
- Duplicate clock-in prevention
- Successful worker clock-out
- Overtime calculation above 8 hours
- Monthly overtime cap enforcement (60 hours)
- Current-month settlement rejection
- Successful historical-month settlement
- Atomic settlement processing
- Active worker cache updates
- Redis unavailable startup behavior
- Attendance history pagination
- N+1 query optimization verification using Hibernate SQL logs

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

# Assignment Ticket Mapping

| Ticket | Description | Status |
|----------|-------------|---------|
| LF-201 | CORS Configuration | Completed |
| LF-202 | Redis Resilience | Completed |
| LF-203 | Pagination & N+1 Fix | Completed |
| LF-204 | Atomic Settlement Transactions | Completed |
| LF-205 | Connection Pool Optimization | Completed |

---

# Database Schema Overview

## Worker

| Column | Description |
|----------|-------------|
| id | Primary Key |
| name | Worker Name |
| phone | Contact Number |
| designation | Worker Designation |
| daily_wage_rate | Daily Wage |
| active | Active Status |

## Site

| Column | Description |
|----------|-------------|
| id | Primary Key |
| site_name | Site Name |
| location | Site Location |
| active | Active Status |

## AttendanceLog

| Column | Description |
|----------|-------------|
| worker_id | Worker Reference |
| site_id | Site Reference |
| clock_in | Shift Start |
| clock_out | Shift End |
| total_hours | Worked Hours |
| overtime_hours | Calculated Overtime |
| flagged | Review Flag |

## OvertimeEntry

| Column | Description |
|----------|-------------|
| worker_id | Worker Reference |
| attendance_id | Attendance Reference |
| overtime_date | Overtime Date |
| overtime_hours | Overtime Hours |
| amount | Payout Amount |
| settlement_status | Pending / Settled |

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
laborforce-hrms.postman_collection.json
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

# What I Would Improve With More Time

- JWT-based authentication and authorization
- Role-based access control for supervisors and payroll operators
- SMS retry queue using Redis Streams or RabbitMQ
- Audit trail for settlement operations
- Distributed locking for high-volume attendance processing
- Prometheus and Grafana monitoring
- Integration testing using Testcontainers
- Kubernetes deployment manifests

# Lessons Learned

- Cache should improve performance but never become a system dependency.
- Notifications should never occur before transaction commits.
- Pagination alone does not solve N+1 query problems.
- Database transactions should not contain slow external network calls.
- Payroll systems prioritize correctness over convenience.
- Business rules belong in both application and persistence layers.

---

# Author
Java Backend Developer Assignment Submission (2026)