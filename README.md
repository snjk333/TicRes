# 🎫 TicRes - Event Ticketing Platform

A fully-functional distributed system for managing event ticket sales. **Diploma Project Evolution** - consolidation of three independent microservices into a single monorepo with complete integration, enhanced security, and production-ready architecture.

> **Evolution of Projects:**
> - [snjk333/Monolith](https://github.com/snjk333/Monolith) → main business logic
> - [snjk333/EventProvider](https://github.com/snjk333/EventProvider) → event and ticket management
> - [snjk333/RegisterMS](https://github.com/snjk333/RegisterMS) → authentication and authorization

---

## 📋 Table of Contents

- [Architecture](#architecture)
- [Quick Start](#quick-start)
- [API Endpoints](#api-endpoints)
- [Tech Stack](#tech-stack)
- [System Components](#system-components)
- [Security](#security)
- [Testing](#testing)
- [Development](#development)

---

## 🏗️ Architecture

### Microservices Architecture with API Gateway

```
┌─────────────────────────────────────────────────────────────┐
│                    Frontend / Client                         │
└──────────────────────────┬──────────────────────────────────┘
                           │
                    ┌──────▼──────┐
                    │ API Gateway  │ (Port 8000)
                    │ (Spring Cloud Gateway)
                    └──────┬──────┘
         ┌──────────────────┼──────────────────┐
         │                  │                  │
    ┌────▼────┐      ┌─────▼──────┐     ┌────▼──────┐
    │RegisterMS│      │  Monolith  │     │EventProvider│
    │(Port 8080)      │ (Port 8088)│     │(Port 8081)
    │Reactive │      │Synchronous │     │Synchronous│
    │R2DBC    │      │JPA/Hibernate      │JPA      │
    └────┬────┘      └─────┬──────┘     └────┬─────┘
         │                  │                  │
         └──────────────────┼──────────────────┘
                            │
                      ┌─────▼──────┐
                      │ PostgreSQL  │
                      │ (5432)      │
                      │ 3 Databases │
                      └─────────────┘
```

### Components

| Service | Port | Stack | Functions |
|---------|------|-------|-----------|
| **API Gateway** | 8000 | Spring Cloud Gateway | Routing, CORS, load balancing |
| **RegisterMS** | 8080 | Spring WebFlux, R2DBC | JWT auth, registration, refresh tokens |
| **Monolith** | 8088 | Spring Web, JPA | Bookings, payments (PayU), events |
| **EventProvider** | 8081 | Spring Web, JPA | Event synthesis, ticket management |
| **PostgreSQL** | 5432 | PostgreSQL 16 | 3 Databases: diploma, diploma_registerms, diploma_eventprovider |

---

## 🚀 Quick Start

### Requirements

- **Java 17+**
- **Maven 3.8+**
- **Docker & Docker Compose** (optional)
- **PostgreSQL 16** (or via Docker)

### Local Setup Without Docker

#### 1️⃣ Clone Repository

```bash
git clone https://github.com/snjk333/TicRes.git
cd TicRes
```

#### 2️⃣ Database Setup

```bash
# PostgreSQL must be running on localhost:5432
# Create 3 databases:
psql -U postgres
CREATE DATABASE diploma;
CREATE DATABASE diploma_registerms;
CREATE DATABASE diploma_eventprovider;
```

#### 3️⃣ Build Project

```bash
mvn clean install
```

#### 4️⃣ Run Services (in separate terminals)

```bash
# Terminal 1: RegisterMS
cd RegisterMS && mvn spring-boot:run

# Terminal 2: Monolith
cd Monolith && mvn spring-boot:run

# Terminal 3: EventProvider
cd EventProvider && mvn spring-boot:run

# Terminal 4: API Gateway
cd ApiGateway && mvn spring-boot:run
```

### Docker Compose

```bash
# Requires .env file with environment variables
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

---

## 📡 API Endpoints

### 🔐 Authentication (RegisterMS)

#### Register
```http
POST /auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+38067123456"
}

Response: 201 Created
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "expiresIn": 3600
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "SecurePass123!"
}

Response: 200 OK
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "expiresIn": 3600
}
```

#### Refresh Token
```http
POST /auth/refresh
Cookie: refreshToken=550e8400-e29b-41d4-a716-446655440000

Response: 200 OK
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "new-uuid-token"
}
```

#### Logout
```http
POST /auth/logout
Authorization: Bearer eyJhbGc...
Cookie: refreshToken=550e8400-e29b-41d4-a716-446655440000

Response: 204 No Content
```

### 🎫 Events & Tickets (Monolith)

#### Get All Events (Paginated)
```http
GET /monolith/events?page=0&size=10
Authorization: Bearer eyJhbGc...

Response: 200 OK
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "Concert 2025",
      "description": "Amazing concert experience",
      "date": "2025-06-15T20:00:00",
      "location": "Arena Hall",
      "capacity": 5000
    }
  ],
  "totalElements": 42,
  "totalPages": 5,
  "currentPage": 0
}
```

#### Get Event Details
```http
GET /monolith/events/{eventId}
Authorization: Bearer eyJhbGc...

Response: 200 OK
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Concert 2025",
  "description": "Amazing concert experience",
  "date": "2025-06-15T20:00:00",
  "location": "Arena Hall",
  "capacity": 5000,
  "availableTickets": 3420,
  "tickets": [
    {
      "id": "ticket-1",
      "type": "VIP",
      "price": 150.00,
      "place": "Section A",
      "status": "AVAILABLE"
    }
  ]
}
```

#### Get Event Tickets
```http
GET /monolith/events/{eventId}/tickets
Authorization: Bearer eyJhbGc...

Response: 200 OK
[
  {
    "id": "ticket-1",
    "eventId": "event-1",
    "type": "VIP",
    "price": 150.00,
    "place": "Section A",
    "status": "AVAILABLE"
  },
  {
    "id": "ticket-2",
    "eventId": "event-1",
    "type": "Standard",
    "price": 75.00,
    "place": "Section B",
    "status": "RESERVED"
  }
]
```

### 📅 Bookings (Monolith)

#### Create Booking
```http
POST /monolith/bookings
Authorization: Bearer eyJhbGc...
Content-Type: application/json

{
  "ticketId": "ticket-1"
}

Response: 200 OK
{
  "id": "booking-1",
  "ticketId": "ticket-1",
  "status": "CREATED",
  "createdAt": "2025-12-08T10:30:00"
}
```

#### Get My Bookings
```http
GET /monolith/bookings/my
Authorization: Bearer eyJhbGc...

Response: 200 OK
[
  {
    "id": "booking-1",
    "ticketId": "ticket-1",
    "status": "CREATED",
    "createdAt": "2025-12-08T10:30:00"
  }
]
```

#### Get Booking Details
```http
GET /monolith/bookings/{bookingId}
Authorization: Bearer eyJhbGc...

Response: 200 OK
{
  "id": "booking-1",
  "user": {
    "id": "user-1",
    "username": "john_doe",
    "email": "john@example.com"
  },
  "ticket": {
    "id": "ticket-1",
    "type": "VIP",
    "price": 150.00,
    "place": "Section A"
  },
  "status": "CREATED",
  "createdAt": "2025-12-08T10:30:00"
}
```

#### Initiate Payment
```http
POST /monolith/bookings/{bookingId}/pay
Authorization: Bearer eyJhbGc...

Response: 200 OK
{
  "redirectUri": "https://secure.payu.com/api/v2_1/checkout?orderId=..."
}
```

#### Cancel Booking
```http
PUT /monolith/bookings/{bookingId}/cancel
Authorization: Bearer eyJhbGc...

Response: 200 OK
{
  "id": "booking-1",
  "status": "CANCELLED",
  "createdAt": "2025-12-08T10:30:00"
}
```

---

## 💻 Tech Stack

### Backend

| Component | Version | Purpose |
|-----------|---------|---------|
| **Spring Boot** | 3.2.0 | Framework |
| **Spring Cloud Gateway** | 2023.0.0 | API Gateway |
| **Spring WebFlux** | 3.2.0 | Reactive programming (RegisterMS) |
| **Spring Data JPA** | 3.2.0 | ORM (Monolith, EventProvider) |
| **Spring Data R2DBC** | 3.2.0 | Reactive database access (RegisterMS) |
| **Spring Security** | 3.2.0 | Authentication & authorization |
| **Flyway** | 9.22.3 | Database migrations |
| **JWT (JJWT)** | 0.11.5 | JWT tokens |
| **PostgreSQL Driver** | 42.7.1 | PostgreSQL client |
| **Lombok** | 1.18.30 | Code generation |
| **MapStruct** | 1.5.5 | DTO mapping |

### Frontend (Optional)

- React / Vue.js / Angular
- Axios / Fetch API
- JWT tokens in localStorage or httpOnly cookies

### DevOps

- **Docker** - containerization
- **Docker Compose** - local orchestration
- **PostgreSQL 16** - primary database
- **Maven** - build management

---

## 🔒 Security

### JWT Authentication

✅ **Implemented:**
- JWT tokens with HS256 signature
- Refresh token mechanism (7 days validity)
- Access token TTL (1 hour)
- Token blacklist on logout
- HttpOnly cookies for refresh tokens

```java
// RegisterMS creates tokens
POST /auth/login
→ AccessToken: eyJhbGc... (JWT in response body)
→ RefreshToken: httpOnly cookie

// Monolith validates token
GET /monolith/bookings
Authorization: Bearer eyJhbGc...
→ JwtAuthFilter verifies signature
→ Checks blacklist
→ Extracts userId from JWT
→ SecurityContext.setAuthentication(userId)
```

### CORS

```yaml
API Gateway:
  allowedOrigins: ["https://yourdomain.com"]  # production
  allowedMethods: [GET, POST, PUT, DELETE, PATCH]
  allowedHeaders: ["*"]
  allowCredentials: true
```

### Race Condition Protection

✅ **Optimistic Locking on Ticket entity**

```java
@Entity
public class Ticket {
    @Version
    private Long version;  // Automatic version checking
}

// During concurrent booking:
// User1 & User2 attempt to reserve same ticket
// ✅ First one updates version successfully
// ❌ Second gets OptimisticLockException
```

### Payment Integration

- **PayU** - secure payment processing
- Webhook notifications for payment events
- Signature verification on all payment requests
- Idempotency checks for webhook processing

---

### Swagger UI

- **RegisterMS**: http://127.0.0.1:8080/swagger-ui.html
- **Monolith**: http://127.0.0.1:8088/monolith/swagger-ui.html
- **EventProvider**: http://127.0.0.1:8081/swagger-ui.html

---

## 🛠️ Development

### Project Structure

```
TicRes/
├── ApiGateway/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── RegisterMS/                 # Authentication Service (Reactive)
│   ├── src/main/java/com/oleksandr/registerms/
│   │   ├── controller/         # REST endpoints
│   │   ├── service/            # Business logic
│   │   ├── repository/         # R2DBC repositories
│   │   ├── security/           # JWT & Security
│   │   ├── entity/             # JPA entities
│   │   └── dto/                # Request/Response DTOs
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── application-secrets.properties
│   │   └── db/migration/       # Flyway migrations
│   ├── pom.xml
│   └── Dockerfile
├── Monolith/                   # Business Logic Service
│   ├── src/main/java/com/oleksandr/monolith/
│   │   ├── rest/               # REST controllers
│   │   ├── Booking/            # Bookings management
│   │   ├── Ticket/             # Tickets management
│   │   ├── Event/              # Events management
│   │   ├── User/               # User profiles
│   │   ├── Configuration/      # Security & CORS config
│   │   ├── integration/payU/   # PayU integration
│   │   └── common/             # Utilities & exceptions
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── db/migration/       # Flyway migrations
│   ├── pom.xml
│   └── Dockerfile
├── EventProvider/              # Events & Tickets Provider
│   ├── src/main/java/com/oleksandr/eventprovider/
│   │   ├── rest/               # REST endpoints
│   │   ├── Event/              # Event management
│   │   ├── Ticket/             # Ticket management
│   │   ├── TicketMaster/       # Ticketmaster API integration
│   │   └── FakeInfo/           # Fake data generation
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── db/migration/
│   ├── pom.xml
│   └── Dockerfile
├── common-lib/                 # Shared DTOs & Enums
│   ├── src/main/java/com/oleksandr/common/
│   │   ├── dto/                # Shared DTOs
│   │   ├── enums/              # Status enums
│   │   └── constants/
│   └── pom.xml
├── loadtest/                   # Load testing scripts
│   ├── raceConditionTest.js    # Race condition test
│   ├── loadtestRegistration.js # Registration stress test
│   ├── loadtest.js             # General load test
│   └── package.json
├── docker-compose.yml          # Multi-container setup
├── init-databases.sh           # Database initialization
├── pom.xml                     # Root pom (Maven multi-module)
└── README.md                   # This file
```

### Adding New Feature

1. **Create feature branch**
   ```bash
   git checkout -b feature/new-feature
   ```

2. **Development**
   ```bash
   # Modify code in appropriate module
   # Write tests
   # Update DB migrations if needed
   ```

3. **Testing**
   ```bash
   mvn clean test
   ```

4. **Commit & Push**
   ```bash
   git add .
   git commit -m "feat: description of new feature"
   git push origin feature/new-feature
   ```

5. **Pull Request** on GitHub

### Database Migrations

Uses **Flyway** for database schema versioning.

```bash
# Migrations located in:
# - RegisterMS/src/main/resources/db/migration/
# - Monolith/src/main/resources/db/migration/
# - EventProvider/src/main/resources/db/migration/

# File naming: V{version}__{description}.sql
# Example: V1__Initial_schema.sql
```

### Code Style

- **Java 17** syntax
- **Lombok** for reducing boilerplate
- **MapStruct** for DTO mapping
- **Spring conventions** - CamelCase for variables, PascalCase for classes

---

## 📊 Known Issues & Improvements

### In Development

- [ ] User entity synchronization between RegisterMS and Monolith
- [ ] Event-driven architecture with Kafka/RabbitMQ
- [ ] Full-text search for events (Elasticsearch)
- [ ] Redis caching (events, popular tickets)
- [ ] Rate limiting on API Gateway
- [ ] Analytics and monitoring (ELK Stack)

---

## 📞 Contact & Support

- **Author**: Oleksandr ([GitHub](https://github.com/snjk333))
---

## 📜 License

MIT License - free use for educational purposes.

---

## 🙏 Acknowledgments

Thank you for using TicRes! Your contribution helps improve the project.

### Original Repositories

- [Monolith](https://github.com/snjk333/Monolith) - Original business logic
- [EventProvider](https://github.com/snjk333/EventProvider) - Original events service
- [RegisterMS](https://github.com/snjk333/RegisterMS) - Original auth service

---

### Quick References

| URL | Purpose |
|-----|---------|
| http://localhost:8000 | API Gateway (main entry) |
| http://localhost:8080 | RegisterMS (auth) |
| http://localhost:8088 | Monolith (main service) |
| http://localhost:8081 | EventProvider |
| http://localhost:5432 | PostgreSQL |
| http://localhost:8080/swagger-ui.html | RegisterMS Swagger |
| http://localhost:8088/monolith/swagger-ui.html | Monolith Swagger |
| http://localhost:8081/swagger-ui.html | EventProvider Swagger |
