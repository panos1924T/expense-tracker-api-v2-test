# Expense Tracker API

A headless RESTful API for personal finance management, built as a Coding Factory (OPA) capstone project. Supports multi-user expense tracking across accounts, categories, and transactions — with a focus on correctness, security, and clean architecture.

---

## Tech Stack

- **Java 21** / **Spring Boot 3**
- **PostgreSQL** — relational database
- **Spring Security** — stateless JWT authentication
- **Spring Data JPA** (Hibernate) + `@SQLRestriction` for transparent soft delete
- **BCrypt** — password hashing
- **BigDecimal** — monetary precision
- **Gradle** — build tool

---

## Features

- **JWT Authentication** — stateless, token-based auth with BCrypt password hashing
- **Multi-user isolation** — all resources are scoped to the authenticated user via UUID ownership checks
- **Soft delete** — records are never hard-deleted; `AbstractEntity` base class manages `deleted` flag and `deletedAt` timestamp transparently via `@SQLRestriction`
- **Financial precision** — all monetary values use `BigDecimal`
- **Hybrid exception architecture** — checked exceptions for critical financial operations (insufficient balance, invalid transaction), unchecked for domain errors (not found, unauthorized, duplicate)
- **Clean layering** — DTOs, Mappers, Services, and Repositories are strictly separated; no entity objects leak into the API layer

---

## Domain Model

```
User
 └── Account  (type: LIQUIDITY | SAVINGS | INVESTMENT | CREDIT)
      └── Transaction
           └── Category
```

---

## Project Structure

```
src/main/java/
  entity/         JPA entities (User, Account, Category, Transaction, AbstractEntity)
  repository/     Spring Data JPA repositories
  dto/            Request/response records (CreateDTO, UpdateDTO, ReadOnlyDTO)
  mapper/         Manual entity <-> DTO mapping (mapTo prefix, null-safe)
  service/        Service interfaces + implementations (VALIDATE → PREPARE → EXECUTE → RETURN)
  exception/      6 custom exceptions (AppGenericException base + domain-specific)
  core/           ErrorHandler (@RestControllerAdvice), SecurityConfig
```

---

## Exception Architecture

| Type | Exceptions | HTTP |
|---|---|---|
| Checked (financial) | `InsufficientBalanceException`, `InvalidTransactionException` | 422 |
| Unchecked (domain) | `EntityNotFoundException`, `EntityAlreadyExistsException`, `UnauthorizedException`, `ValidationException` | 404 / 409 / 401 / 400 |

All exceptions extend either `Exception` (financial) or `AppGenericException extends RuntimeException` (domain).

---

## Setup

### Requirements

- Java 21
- PostgreSQL 15+
- Gradle (wrapper included)

### Database

Create a PostgreSQL database and configure the connection in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/expense_tracker
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### Run

```bash
./gradlew bootRun       # Start the application
./gradlew build         # Build the project
./gradlew test          # Run tests
```

The server starts on port **8080**.

---

## API Overview

Base path: `/api/v1`

> All endpoints (except registration and login) require a valid JWT:
> `Authorization: Bearer <token>`

### Auth

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/auth/register` | Public | Register a new user |
| POST | `/auth/login` | Public | Authenticate and receive JWT |

### Accounts

| Method | Endpoint | Description |
|---|---|---|
| POST | `/accounts` | Create account |
| GET | `/accounts/{uuid}` | Get account by UUID |
| PUT | `/accounts/{uuid}` | Update account |
| DELETE | `/accounts/{uuid}` | Soft-delete account |

### Categories

| Method | Endpoint | Description |
|---|---|---|
| POST | `/categories` | Create category |
| GET | `/categories/{uuid}` | Get category |
| PUT | `/categories/{uuid}` | Update category |
| DELETE | `/categories/{uuid}` | Soft-delete category |

### Transactions

| Method | Endpoint | Description |
|---|---|---|
| POST | `/transactions` | Create transaction |
| GET | `/transactions/{uuid}` | Get transaction |
| PUT | `/transactions/{uuid}` | Update transaction |
| DELETE | `/transactions/{uuid}` | Soft-delete transaction (with balance reversal) |

---

## Design Decisions

- **UUID as external identifier** — internal `id` columns are never exposed in the API
- **Service method pattern** — every service method follows: VALIDATE → PREPARE → EXECUTE → RETURN
- **Soft delete with balance reversal** — deleting a transaction reverses its effect on the account balance within the same `@Transactional` operation
- **DTOs as Java records** — immutable, concise, with Jakarta Validation annotations
- **No entity objects in DTOs** — strict separation to avoid leaking persistence concerns into the API layer

---

## Status

🚧 Work in progress — developed as a Coding Factory (OPA) capstone project.
