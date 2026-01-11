# 🎫 TicRes - Event Ticketing Platform

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)
![Status](https://img.shields.io/badge/status-in%20development-yellow.svg)

A **microservices platform** for event ticket booking and sales with real payment gateway integration (PayU), JWT authentication, and automated event synchronization from Ticketmaster API.

## 🎯 What Does It Do?

TicRes is a complete ticketing system that:
- **Authenticates users** with JWT tokens and refresh mechanism
- **Imports real events** from Ticketmaster API automatically
- **Manages ticket booking** with race condition protection
- **Processes payments** through PayU payment gateway
- **Sends email notifications** for registrations and purchases
- **Prevents double-booking** with optimistic locking
- **Auto-cancels expired bookings** after 15 minutes without payment
- **Provides API Gateway** as single entry point for all services

**Evolution from three separate projects:**
- [snjk333/Monolith](https://github.com/snjk333/Monolith) → business logic
- [snjk333/EventProvider](https://github.com/snjk333/EventProvider) → event management
- [snjk333/RegisterMS](https://github.com/snjk333/RegisterMS) → authentication

---

## 📋 Table of Contents

- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Tech Stack](#tech-stack)
- [Known Issues](#known-issues)
- [Roadmap](#roadmap)
- [License](#license)
- [Project Status](#project-status)

---

## 🏗️ Architecture

### Microservices Architecture

```
                         ┌─────────────┐
                         │   Client    │
                         └──────┬──────┘
                                │
                         ┌──────▼──────────┐
                         │  API Gateway    │ :8000
                         │ (Spring Cloud)  │
                         └──────┬──────────┘
                    ┌───────────┼───────────┐
                    │           │           │
            ┌───────▼───┐ ┌─────▼──────┐ ┌─▼──────────┐
            │ RegisterMS│ │  Monolith  │ │EventProvider│
            │   :8080   │ │   :8088    │ │   :8081    │
            │ (Reactive)│ │(Blocking)  │ │ (Blocking) │
            │  WebFlux  │ │  MVC+JPA   │ │  MVC+JPA   │
            └─────┬─────┘ └─────┬──────┘ └─────┬──────┘
                  │             │              │
                  │    ┌────────┼────────┐     │
                  │    │        │        │     │
            ┌─────▼────▼──┐ ┌───▼───┐ ┌─▼─────▼─────┐
            │  PostgreSQL │ │ Kafka │ │Ticketmaster │
            │ 3 Databases │ │ :9092 │ │   API       │
            └─────────────┘ └───┬───┘ └─────────────┘
                                │
                        ┌───────▼────────┐
                        │ notificationSvc│
                        │     :8085      │
                        │  Email Sender  │
                        └────────────────┘
```

### System Flow

**1. User Registration:**
```
Client → ApiGateway → RegisterMS → Kafka → notificationService → Email
```

**2. Event Synchronization (every 2 min):**
```
Ticketmaster API → EventProvider → Monolith (REST polling)
```

**3. Booking & Payment:**
```
Client → ApiGateway → Monolith → PayU API
PayU Webhook → Ngrok → ApiGateway → Monolith → Kafka → Email
```

### Services Overview

| Service | Port | Technology | Database | Purpose |
|---------|------|------------|----------|---------|
| **ApiGateway** | 8000 | Spring Cloud Gateway | - | Routes all requests, CORS handling |
| **RegisterMS** | 8080 | WebFlux + R2DBC | `Diploma` | Reactive auth service, JWT management |
| **Monolith** | 8088 | Spring MVC + JPA | `DiplomaMonolith` | Main business logic, bookings, PayU |
| **EventProvider** | 8081 | Spring MVC + JPA | `DiplomaEventProvider` | Ticketmaster sync, event source |
| **notificationService** | 8085 | Spring Boot + Kafka | - | Email notifications via SMTP |
| **PostgreSQL** | 5432 | PostgreSQL 16 | 3 DBs | All persistent data |
| **Kafka** | 9092 | Apache Kafka | - | Async messaging for emails |

---

## � Prerequisites

Before installation, ensure you have:

### Required
- **Java 17** or higher ([Download](https://adoptium.net/))
- **Maven 3.8+** ([Download](https://maven.apache.org/download.cgi))

### Optional (for local development without Docker)
- **PostgreSQL 16** ([Download](https://www.postgresql.org/download/))
- **Apache Kafka 3.x** ([Download](https://kafka.apache.org/downloads))

### Recommended
- **Docker & Docker Compose** for containerized setup
- **Ngrok** account for PayU webhook testing ([Sign up](https://ngrok.com/))
- **PayU account** for payment processing ([PayU Developers](https://developers.payu.com/))
- **Ticketmaster API key** ([Get API Key](https://developer.ticketmaster.com/))

### Verify Installation

```bash
# Check Java version
java -version
# Output should be: openjdk version "17.x.x"

# Check Maven
mvn -version

# Check PostgreSQL
psql --version

# Check Docker (optional)
docker --version
docker-compose --version
```

---

## 🚀 Installation

### Method 1: Local Development (Recommended for Development)

#### Step 1: Clone Repository

```bash
git clone https://github.com/snjk333/TicRes.git
cd TicRes
```

#### Step 2: Setup PostgreSQL

Create three databases:

```bash
# Connect to PostgreSQL
psql -U postgres

# Create databases
CREATE DATABASE "Diploma";
CREATE DATABASE "DiplomaMonolith";
CREATE DATABASE "DiplomaEventProvider";

# Exit
\q
```

#### Step 3: Configure Environment Variables

Create `.env` file in project root or export variables:

```bash
# PostgreSQL
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_password
POSTGRES_PORT=5432

# RegisterMS Database
REGISTER_MS_R2DBC_URL=r2dbc:postgresql://localhost:5432/Diploma
REGISTER_MS_R2DBC_USERNAME=postgres
REGISTER_MS_R2DBC_PASSWORD=your_password
REGISTER_MS_FLYWAY_URL=jdbc:postgresql://localhost:5432/Diploma
REGISTER_MS_FLYWAY_USER=postgres
REGISTER_MS_FLYWAY_PASSWORD=your_password

# Monolith Database
MONOLITH_DATASOURCE_URL=jdbc:postgresql://localhost:5432/DiplomaMonolith
MONOLITH_DATASOURCE_USERNAME=postgres
MONOLITH_DATASOURCE_PASSWORD=your_password

# EventProvider Database
EVENT_PROVIDER_DATASOURCE_URL=jdbc:postgresql://localhost:5432/DiplomaEventProvider
EVENT_PROVIDER_DATASOURCE_USERNAME=postgres
EVENT_PROVIDER_DATASOURCE_PASSWORD=your_password

# JWT Secret (generate random string)
JWT_SECRET=your-super-secret-jwt-key-min-256-bits
JWT_SECRET_KEY=your-super-secret-jwt-key-min-256-bits

# PayU Configuration (get from https://developers.payu.com/)
PAYU_CLIENT_ID=your_payu_client_id
PAYU_CLIENT_SECRET=your_payu_client_secret
PAYU_MERCHANT_POS_ID=your_pos_id
PAYU_SECOND_KEY=your_second_key
PAYU_BASE_URL=https://secure.snd.payu.com/api/v2_1  # sandbox
PAYU_NOTIFY_BASE_URL=https://your-ngrok-url.ngrok.io  # for webhooks

# Ticketmaster API (get from https://developer.ticketmaster.com/)
TICKETMASTER_API_KEY=your_ticketmaster_api_key

# Email (SMTP) - for notificationService
NOTIFICATION_SPRING_MAIL_USERNAME=your-email@gmail.com
NOTIFICATION_SPRING_MAIL_PASSWORD=your-app-password

# Frontend URL
APP_FRONTEND_URL=http://localhost:3000

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

#### Step 4: Build Project

```bash
# Build all modules
mvn clean install -DskipTests

# Or build with tests
mvn clean install
```

#### Step 5: Run Services

Open **5 separate terminals** and run:

```bash
# Terminal 1: API Gateway
cd ApiGateway
mvn spring-boot:run

# Terminal 2: RegisterMS
cd RegisterMS
mvn spring-boot:run

# Terminal 3: EventProvider
cd EventProvider
mvn spring-boot:run

# Terminal 4: Monolith
cd Monolith
mvn spring-boot:run

# Terminal 5: notificationService
cd notificationService
mvn spring-boot:run
```

#### Step 6: Verify Services

```bash
# Check health of all services
curl http://localhost:8000/actuator/health  # API Gateway
curl http://localhost:8080/actuator/health  # RegisterMS
curl http://localhost:8088/monolith/actuator/health  # Monolith
curl http://localhost:8081/actuator/health  # EventProvider
```

---

### Method 2: Docker Compose (Recommended for Quick Start)

✅ **Includes all services:** PostgreSQL, Kafka (KRaft mode), all microservices, and Ngrok.

```bash
# 1. Create .env file with all variables (see Step 3 above)

# 2. Start all services
docker-compose up -d

# 3. Check logs
docker-compose logs -f

# 4. Check specific service
docker-compose logs -f ticres-kafka

# 5. Stop services
docker-compose down

# 6. Stop and remove volumes (clean start)
docker-compose down -v
```

---

## ⚙️ Configuration

### RegisterMS Configuration

File: `RegisterMS/src/main/resources/application.properties`

```properties
# R2DBC (Reactive PostgreSQL)
spring.r2dbc.url=r2dbc:postgresql://localhost:5432/Diploma
spring.r2dbc.username=postgres
spring.r2dbc.password=${REGISTER_MS_R2DBC_PASSWORD}

# Flyway (uses blocking driver for migrations)
spring.flyway.enabled=true
spring.flyway.url=jdbc:postgresql://localhost:5432/Diploma

# JWT
jwt.secret-key=${JWT_SECRET_KEY}
jwt.issuer=auth-service
jwt.expiration-seconds=3600  # 1 hour

# Monolith URL for blacklist sync
monolith.url=${MONOLITH_URL:http://localhost:8088}

# Cleanup scheduler (delete expired tokens)
scheduler.cleanup-delay=3600000  # 1 hour
```

### Monolith Configuration

File: `Monolith/src/main/resources/application.properties`

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/DiplomaMonolith
spring.datasource.username=postgres
spring.datasource.password=${MONOLITH_DATASOURCE_PASSWORD}

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Context path
server.servlet.context-path=/monolith

# Event sync scheduler
scheduler.delay=120000  # 2 minutes

# PayU
payu.client.id=${PAYU_CLIENT_ID}
payu.client.secret=${PAYU_CLIENT_SECRET}
payu.merchant.pos.id=${PAYU_MERCHANT_POS_ID}
payu.second.key=${PAYU_SECOND_KEY}
payu.base.url=${PAYU_BASE_URL}
payu.notify.url=${PAYU_NOTIFY_BASE_URL}/monolith/api/payu/notifications

# JWT
jwt.secret=${JWT_SECRET}

# External services
auth.service.url=${AUTH_SERVICE_URL:http://localhost:8080}
event.provider.url=http://localhost:8081
```

### EventProvider Configuration

File: `EventProvider/src/main/resources/application.properties`

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/DiplomaEventProvider
spring.datasource.username=postgres
spring.datasource.password=${EVENT_PROVIDER_DATASOURCE_PASSWORD}

# Ticketmaster API
ticketmaster.api.baseurl=https://app.ticketmaster.com/discovery/v2
ticketmaster.api.key=${TICKETMASTER_API_KEY}
```

### notificationService Configuration

File: `notificationService/src/main/resources/application.properties`

```properties
# SMTP (Gmail example)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${NOTIFICATION_SPRING_MAIL_USERNAME}
spring.mail.password=${NOTIFICATION_SPRING_MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=mail_consumer
```

---

## 💻 Usage

### Complete User Journey Example

#### 1. Register New User

```bash
curl -X POST http://localhost:8000/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "SecurePass123!",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+48123456789"
  }'
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "expiresIn": 3600
}
```

✉️ User receives welcome email via Kafka → notificationService

#### 2. Get Events List

```bash
curl http://localhost:8000/monolith/events?page=0&size=10 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Response:**
```json
{
  "content": [
    {
      "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "name": "Ed Sheeran Concert",
      "description": "Live performance in Warsaw",
      "location": "National Stadium, Warsaw",
      "eventDate": "2026-06-15T20:00:00",
      "imageUrl": "https://example.com/image.jpg"
    }
  ],
  "totalElements": 42,
  "totalPages": 5,
  "number": 0,
  "size": 10
}
```

#### 3. Get Available Tickets

```bash
curl http://localhost:8000/monolith/events/3fa85f64-5717-4562-b3fc-2c963f66afa6/tickets \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Response:**
```json
[
  {
    "id": "ticket-uuid-1",
    "eventId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "type": "VIP",
    "price": 350.00,
    "place": "Section A, Row 5, Seat 12",
    "status": "AVAILABLE"
  },
  {
    "id": "ticket-uuid-2",
    "eventId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "type": "Standard",
    "price": 150.00,
    "place": "Section B, Row 10, Seat 5",
    "status": "AVAILABLE"
  }
]
```

#### 4. Create Booking

```bash
curl -X POST http://localhost:8000/monolith/bookings \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "ticketId": "ticket-uuid-1"
  }'
```

**Response:**
```json
{
  "id": "booking-uuid",
  "ticketId": "ticket-uuid-1",
  "status": "CREATED",
  "createdAt": "2026-01-11T14:30:00",
  "version": 0
}
```

🎫 Ticket status changes: `AVAILABLE` → `RESERVED`

#### 5. Initiate Payment

```bash
curl -X POST http://localhost:8000/monolith/bookings/booking-uuid/pay \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Response:**
```json
{
  "redirectUri": "https://secure.payu.com/api/v2_1/orders/checkout?orderId=XYZ123"
}
```

💳 User is redirected to PayU payment page
📊 Booking status: `CREATED` → `WAITING_FOR_PAYMENT`

#### 6. Payment Webhook (Automatic)

After successful payment, PayU sends webhook:

```
POST /monolith/api/payu/notifications
```

System automatically:
- ✅ Verifies PayU signature (MD5)
- ✅ Checks for duplicate notifications
- ✅ Updates booking status: `WAITING_FOR_PAYMENT` → `PAID`
- ✅ Updates ticket status: `RESERVED` → `SOLD`
- ✅ Sends confirmation email with ticket

#### 7. View My Bookings

```bash
curl http://localhost:8000/monolith/bookings/my \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Response:**
```json
[
  {
    "id": "booking-uuid",
    "ticketId": "ticket-uuid-1",
    "status": "PAID",
    "createdAt": "2026-01-11T14:30:00",
    "version": 1
  }
]
```

#### 8. Cancel Booking (if not paid)

```bash
curl -X PUT http://localhost:8000/monolith/bookings/booking-uuid/cancel \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

🎫 Ticket returns to `AVAILABLE` status

#### 9. Logout

```bash
curl -X POST http://localhost:8000/auth/logout \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Cookie: refreshToken=YOUR_REFRESH_TOKEN"
```

🔒 Token added to blacklist and cannot be reused

---

### Automated Background Jobs

The system automatically performs:

1. **Event Synchronization** (every 2 minutes)
   - Fetches events from Ticketmaster API
   - Updates EventProvider database
   - Syncs to Monolith database

2. **Booking Expiration** (every 5 minutes)
   - Finds bookings in `WAITING_FOR_PAYMENT` status older than 15 minutes
   - Auto-cancels expired bookings
   - Releases reserved tickets back to `AVAILABLE`

3. **Token Cleanup** (every hour)
   - Removes expired tokens from blacklist

---

## 📡 API Endpoints

### Base URLs

| Service | Base URL | Swagger UI |
|---------|----------|------------|
| API Gateway | `http://localhost:8000` | - |
| RegisterMS | `http://localhost:8080` | [Swagger](http://localhost:8080/swagger-ui.html) |
| Monolith | `http://localhost:8088/monolith` | [Swagger](http://localhost:8088/monolith/swagger-ui.html) |
| EventProvider | `http://localhost:8081` | [Swagger](http://localhost:8081/swagger-ui.html) |

---

### 🔐 Authentication Endpoints

All auth endpoints are routed through API Gateway at `http://localhost:8000/auth/**`

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

All endpoints require `Authorization: Bearer <token>` header

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

### Core Technologies

| Component | Version | Purpose |
|-----------|---------|---------|
| **Java** | 17 | Programming language |
| **Spring Boot** | 3.2.0 | Application framework |
| **Maven** | 3.8+ | Build & dependency management |

### Microservices Frameworks

| Technology | Usage | Where |
|------------|-------|-------|
| **Spring Cloud Gateway** | API Gateway routing | ApiGateway |
| **Spring WebFlux** | Reactive programming | RegisterMS |
| **Spring Web MVC** | Blocking REST APIs | Monolith, EventProvider |
| **Spring Data JPA** | Database ORM | Monolith, EventProvider |
| **Spring Data R2DBC** | Reactive database | RegisterMS |
| **Spring Security** | Authentication | RegisterMS, Monolith |

### Databases & Persistence

| Technology | Purpose |
|------------|---------|
| **PostgreSQL 16** | Primary database (3 separate DBs) |
| **Flyway** | Database migrations (RegisterMS) |
| **Hibernate** | JPA implementation (Monolith, EventProvider) |
| **R2DBC PostgreSQL** | Reactive database driver (RegisterMS) |

### Messaging & Integration

| Technology | Purpose |
|------------|---------|
| **Apache Kafka** | Async messaging for email notifications |
| **WebClient** | HTTP client for service-to-service calls |
| **Spring Kafka** | Kafka integration |

### Security

| Technology | Purpose |
|------------|---------|
| **JWT (JJWT)** | JSON Web Tokens for authentication |
| **BCrypt** | Password hashing |
| **Spring Security** | Security framework |
| **MD5 Signature** | PayU webhook verification |

### External Integrations

| Service | Purpose | Documentation |
|---------|---------|---------------|
| **PayU** | Payment processing | [PayU Docs](https://developers.payu.com/) |
| **Ticketmaster API** | Event data source | [Ticketmaster API](https://developer.ticketmaster.com/) |
| **Gmail SMTP** | Email delivery | - |
| **Ngrok** | Webhook tunneling (dev) | [Ngrok](https://ngrok.com/) |

### Development Tools

| Tool | Purpose |
|------|---------|
| **Lombok** | Reduce boilerplate code |
| **MapStruct** | DTO mapping (planned) |
| **SpringDoc OpenAPI** | API documentation (Swagger) |
| **Docker** | Containerization |
| **Docker Compose** | Multi-container orchestration |

### Project Structure

```
TicRes/ (Maven Multi-Module)
├── pom.xml (Parent POM)
├── common-lib/ (Shared DTOs & Enums)
├── ApiGateway/ (Spring Cloud Gateway)
├── RegisterMS/ (Auth Service - Reactive)
├── Monolith/ (Business Logic - Blocking)
├── EventProvider/ (Events Source - Blocking)
└── notificationService/ (Email Service)
```

---

## 🔒 Security Features

### Authentication & Authorization

✅ **JWT-based Authentication**
- Access tokens valid for 1 hour
- Refresh tokens stored in HTTP-only cookies (7 days)
- HS256 signature algorithm
- Token blacklist on logout

✅ **Password Security**
- BCrypt hashing with salt
- Minimum password requirements enforced

✅ **Token Management**
- Automatic token refresh mechanism
- Blacklist synchronization between services
- Expired token cleanup (hourly job)

### API Security

✅ **Request Validation**
- Input validation on all endpoints
- SQL injection prevention (JPA/R2DBC parameterized queries)
- XSS protection headers

✅ **CORS Configuration**
- Configurable allowed origins
- Credential support for cookies
- Pre-flight request handling

### Payment Security

✅ **PayU Integration**
- OAuth2 authentication with PayU API
- MD5 signature verification on webhooks
- Idempotency through `processed_payu_notifications` table
- HTTPS-only communication

### Data Protection

✅ **Race Condition Prevention**
- Optimistic locking on tickets (`@Version`)
- Database transactions with proper isolation
- Unique constraints on critical fields

✅ **Business Logic Protection**
- User can only access their own bookings
- Ticket status validation before operations
- Booking expiration (15 minutes without payment)

---

## 🐛 Troubleshooting

### Common Issues

#### 1. "Connection refused" to PostgreSQL

```bash
# Check if PostgreSQL is running
sudo systemctl status postgresql  # Linux
brew services list | grep postgresql  # Mac

# Verify databases exist
psql -U postgres -l

# Check connection
psql -U postgres -d Diploma -c "SELECT 1;"
```

#### 2. Kafka connection errors

```bash
# If using Docker Compose (recommended):
# Check Kafka container status
docker-compose ps ticres-kafka

# Check Kafka logs
docker-compose logs -f ticres-kafka

# Restart Kafka if needed
docker-compose restart ticres-kafka

# If running Kafka locally:
# Verify Kafka is running
jps | grep Kafka

# Check topic exists
bin/kafka-topics.sh --list --bootstrap-server localhost:9092

# Recreate topic if needed
bin/kafka-topics.sh --create --topic mailNotifications \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

#### 3. PayU webhook not receiving

```bash
# Start ngrok
ngrok http 8000

# Copy ngrok URL to .env
PAYU_NOTIFY_BASE_URL=https://your-subdomain.ngrok.io

# Restart Monolith service
cd Monolith && mvn spring-boot:run
```

#### 4. JWT token expired

```bash
# Use refresh token endpoint
curl -X POST http://localhost:8000/auth/refresh \
  -H "Cookie: refreshToken=YOUR_REFRESH_TOKEN"
```

#### 5. Email not sending

Check SMTP credentials and settings:

```properties
# For Gmail, use App Password, not regular password
# Enable 2FA and generate app password at:
# https://myaccount.google.com/apppasswords
```

---

## 📁 Project Structure

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

```

---

## 👨‍💻 Author

**Oleksandr**
- GitHub: [@snjk333](https://github.com/snjk333)
- LinkedIn: [Add your LinkedIn if available]
- Email: [Add contact email if public]

---

## 📜 License

This project is licensed under the **MIT License**.

Free to use for educational and commercial purposes.

---

## 🚧 Project Status

**Status:** 🟡 **Active Development**

This project is actively being developed as a diploma project. The core functionality is complete and working:

✅ **Completed:**
- Full authentication system with JWT
- Event synchronization from Ticketmaster
- Ticket booking with race condition protection
- Payment processing through PayU
- Email notifications via Kafka
- API Gateway with routing
- Swagger documentation
- Kafka integration in Docker Compose (KRaft mode)

🚧 **In Progress:**
- Improving error handling
- Writing comprehensive tests
- Documentation enhancements
- Performance optimization

---


## 🔗 Quick Links

### Documentation
- [API Endpoints](#api-endpoints)
- [Configuration Guide](#configuration)
- [Architecture Overview](#architecture)

### External Resources
- [Original Monolith Repository](https://github.com/snjk333/Monolith)
- [Original RegisterMS Repository](https://github.com/snjk333/RegisterMS)
- [Original EventProvider Repository](https://github.com/snjk333/EventProvider)

### Developer Tools
- [RegisterMS Swagger UI](http://localhost:8080/swagger-ui.html)
- [Monolith Swagger UI](http://localhost:8088/monolith/swagger-ui.html)
- [EventProvider Swagger UI](http://localhost:8081/swagger-ui.html)
- [Ngrok Dashboard](http://localhost:4040) (when running)

---

**Made with ☕ and 💻 by Oleksandr**
