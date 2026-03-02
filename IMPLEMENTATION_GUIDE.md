# Complete Implementation Guide: Authentication & Authorization

## Quick Summary

This project now includes a complete **OTP-based authentication system** with **JWT tokens** for a microservices architecture.

- **Auth Service**: Port 8083
- **Database**: PostgreSQL (auth_service_db)
- **Security**: OTP + JWT (HS512)
- **Integration**: All microservices support JWT validation

---

## Files Created

### Core Auth Service Files

```
auth-service/
├── pom.xml                                      # Dependencies (JWT, Security, PostgreSQL)
├── src/main/java/com/ecommerce/auth/
│   ├── AuthServiceApplication.java              # Main Spring Boot class
│   ├── controller/
│   │   └── AuthController.java                  # REST endpoints
│   ├── service/
│   │   ├── AuthService.java                     # Orchestration service
│   │   ├── UserService.java                     # User management
│   │   ├── OTPService.java                      # OTP generation/validation
│   │   └── TokenService.java                    # JWT token management
│   ├── model/
│   │   ├── User.java                            # User entity
│   │   ├── OTP.java                             # OTP entity
│   │   └── AuthToken.java                       # Token entity
│   ├── repository/
│   │   ├── UserRepository.java                  # User persistence
│   │   ├── OTPRepository.java                   # OTP persistence
│   │   └── AuthTokenRepository.java             # Token persistence
│   ├── dto/
│   │   ├── SendOtpRequest.java                  # OTP request DTO
│   │   ├── VerifyOtpRequest.java                # Verify OTP request DTO
│   │   ├── AuthResponse.java                    # Auth response DTO
│   │   ├── ApiResponse.java                     # Generic API response
│   │   └── UserDTO.java                         # User DTO
│   ├── security/
│   │   ├── JwtUtil.java                         # JWT utility (generate/validate)
│   │   ├── JwtAuthenticationFilter.java         # Authentication filter
│   │   └── MicroserviceJwtValidationFilter.java # Reusable filter for other services
│   ├── config/
│   │   └── SecurityConfig.java                  # Spring Security configuration
│   └── exception/
│       └── GlobalExceptionHandler.java          # Global exception handling
└── src/main/resources/
    └── application.properties                   # Configuration (DB, JWT, OTP)
```

### Documentation Files

```
├── AUTH_SERVICE_GUIDE.md                        # Complete API documentation
├── postman_auth_api_collection.json             # Postman test collection
└── run-all-services-with-auth.sh                # Startup script
```

---

## Step-by-Step Setup & Running

### Step 1: Ensure PostgreSQL is Running

```bash
# Check PostgreSQL is running on 192.168.1.110:5432
psql -h 192.168.1.110 -U postgres -c "SELECT 1"

# If not running, start PostgreSQL (varies by OS)
# Ubuntu/Debian: sudo systemctl start postgresql
# macOS: brew services start postgresql
# Windows: Use PostgreSQL installer or WSL
```

### Step 2: Create Auth Service Database

```bash
psql -h 192.168.1.110 -U postgres -c "CREATE DATABASE auth_service_db;"
```

### Step 3: Build All Services

```bash
cd /home/developer/SOFTWARE/ecommerce-microservices
mvn clean package -DskipTests
```

### Step 4: Run Services from IDE

**In IntelliJ/JetBrains IDE:**

1. **Run Eureka Server First** (Port 8761):
   - Navigate to: `eureka-server/src/main/java/com/ecommerce/eureka/EurekaServerApplication.java`
   - Right-click → **Run 'EurekaServerApplication.main()'**
   - Wait for server to start (check http://localhost:8761)

2. **Run Auth Service** (Port 8083):
   - Navigate to: `auth-service/src/main/java/com/ecommerce/auth/AuthServiceApplication.java`
   - Right-click → **Run 'AuthServiceApplication.main()'**
   - Wait for startup (check logs)

3. **Run Other Services** (Same pattern):
   - Order Service (Port 8082): `order-service/.../OrderServiceApplication`
   - Payment Service (Port 8084): `payment-service/.../PaymentServiceApplication`
   - Inventory Service (Port 8085): `inventory-service/.../InventoryServiceApplication`
   - Notification Service (Port 8090): `notification-service/.../NotificationServiceApplication`

### Step 5: Verify Services Are Running

```bash
# Check Eureka dashboard
curl http://localhost:8761/

# Check Auth Service health
curl http://localhost:8083/actuator/health

# List registered services
curl http://localhost:8761/eureka/apps
```

---

## Testing Authentication Flow

### Using cURL

#### 1. Request OTP

```bash
curl -X POST http://localhost:8083/api/v1/auth/send-otp \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser@example.com"}'
```

**Response:**
```json
{
  "message": "OTP sent successfully to testuser@example.com",
  "success": true,
  "data": "123456"  // OTP code (for testing only, not in production)
}
```

#### 2. Verify OTP & Get Token

```bash
curl -X POST http://localhost:8083/api/v1/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email":"testuser@example.com",
    "otp":"123456"
  }'
```

**Response:**
```json
{
  "message": "Login successful",
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "testuser@example.com",
    "role": "USER",
    "expiresAt": "2026-02-27T14:15:00",
    "message": "Login successful",
    "success": true
  }
}
```

#### 3. Use Token with Order Service

```bash
# Copy token from above response and set it
TOKEN="eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."

curl -X POST http://localhost:8082/api/v1/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "550e8400-e29b-41d4-a716-446655440000",
    "totalAmount": 99.99,
    "shippingAddress": "123 Main St, City",
    "billingAddress": "123 Main St, City",
    "notes": "Express shipping"
  }'
```

#### 4. Validate Token

```bash
curl -X POST http://localhost:8083/api/v1/auth/validate-token \
  -H "Authorization: Bearer $TOKEN"
```

---

### Using Postman

1. **Import Collection:**
   - Open Postman
   - Click **Import** → Select `postman_auth_api_collection.json`

2. **Set Environment Variables:**
   - Click environment icon → Select **Auth API Environment**
   - Set `base_url` = http://localhost:8083
   - Set `order_service_url` = http://localhost:8082

3. **Run Requests in Order:**
   - Send OTP (get OTP code from response)
   - Verify OTP (paste OTP code, get token)
   - Token auto-saves to `{{token}}` variable
   - Use token in other endpoints

---

## Database Schema

### Users Table

```sql
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER',
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### OTPs Table

```sql
CREATE TABLE otps (
    otp_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL,
    code VARCHAR(6) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_used BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Auth Tokens Table

```sql
CREATE TABLE auth_tokens (
    token_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(user_id),
    token TEXT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## Configuration Reference

### Auth Service Properties

**File:** `auth-service/src/main/resources/application.properties`

```properties
# Server
server.port=8083
spring.application.name=auth-service

# Database
spring.datasource.url=jdbc:postgresql://192.168.1.110:5432/auth_service_db
spring.datasource.username=postgres
spring.datasource.password=ind@6117
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update

# JWT (15 minutes = 900000 ms)
jwt.secret=ecommerce-microservices-secret-key-change-in-production-with-long-random-string-min-256-bits
jwt.expiration=900000

# OTP (10 minutes = 600000 ms)
otp.expiration=600000

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.instance.prefer-ip-address=true
```

---

## Production Deployment Checklist

### Security

- [ ] Change `jwt.secret` to a strong random value (256+ bits)
- [ ] Use HTTPS/TLS for all communication
- [ ] Store JWT secret in environment variables, not config files
- [ ] Implement rate limiting on OTP endpoint
- [ ] Add request signing for service-to-service calls
- [ ] Enable CORS only for allowed domains
- [ ] Implement token refresh mechanism
- [ ] Add API key authentication for service endpoints

### Database

- [ ] Use strong PostgreSQL password
- [ ] Enable SSL for database connections
- [ ] Set up database backups
- [ ] Create indexes on frequently queried columns
- [ ] Enable query logging/monitoring

### Monitoring & Logging

- [ ] Log all authentication attempts
- [ ] Monitor failed login attempts (alert on suspicious patterns)
- [ ] Track token usage
- [ ] Implement distributed tracing
- [ ] Set up alerts for service failures

### Operations

- [ ] Load test services
- [ ] Set up horizontal scaling
- [ ] Implement circuit breakers
- [ ] Configure health check endpoints
- [ ] Set up graceful shutdown
- [ ] Document runbooks for common issues

---

## API Response Format

All API responses follow this standard format:

### Success Response

```json
{
  "message": "Success message",
  "success": true,
  "data": {
    // Response data
  }
}
```

### Error Response

```json
{
  "message": "Error description",
  "success": false,
  "error": "Detailed error information"
}
```

### Validation Error

```json
{
  "message": "Validation failed",
  "success": false,
  "error": "{email=Email should be valid}"
}
```

---

## Token Format & Claims

JWT Token Structure:
```
Header.Payload.Signature
```

**Header:**
```json
{
  "alg": "HS512",
  "typ": "JWT"
}
```

**Payload:**
```json
{
  "sub": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "role": "USER",
  "iat": 1709033400,
  "exp": 1709034300
}
```

---

## Integrating with Other Microservices

### Step 1: Add Dependencies

Add to `service-pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
```

### Step 2: Add Configuration

Add same `jwt.secret` to `application.properties`:
```properties
jwt.secret=ecommerce-microservices-secret-key-change-in-production-with-long-random-string-min-256-bits
```

### Step 3: Protect Endpoints

```java
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @PostMapping
    public ResponseEntity<?> createOrder(
        @RequestHeader(value = "Authorization", required = true) String authHeader,
        @RequestBody OrderDTO orderDTO) {
        
        // Extract and validate token
        String token = authHeader.substring(7);
        if (!validateToken(token)) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        
        // Process request
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }
}
```

---

## Troubleshooting

### Issue: OTP Not Being Generated

**Solution:**
- Check logs: `tail -f service-logs/auth-service.log`
- Verify database connection
- Ensure auth_service_db exists

### Issue: JWT Token Invalid

**Solution:**
- Check token expiration time
- Verify jwt.secret is same across services
- Check Bearer token format (must include "Bearer " prefix)

### Issue: Port Already in Use

**Solution:**
```bash
# Kill process on port 8083
lsof -ti:8083 | xargs kill -9
```

### Issue: Database Connection Refused

**Solution:**
```bash
# Check PostgreSQL status
psql -h 192.168.1.110 -U postgres -c "SELECT 1"

# If failed, ensure PostgreSQL is running and accessible
```

---

## Next Steps & Enhancements

1. **Phase 2:** Add OAuth2/OpenID Connect support
2. **Phase 3:** Implement role-based access control (RBAC) with ADMIN/USER/VENDOR roles
3. **Phase 4:** Add multi-factor authentication (MFA)
4. **Phase 5:** Implement token refresh mechanism
5. **Phase 6:** Add social login (Google, GitHub, Facebook)
6. **Phase 7:** Implement API Gateway pattern for centralized auth
7. **Phase 8:** Add audit logging for security events

---

## Support & Documentation

- **API Documentation:** http://localhost:8083/api/v1/auth/swagger-ui.html
- **Main Guide:** AUTH_SERVICE_GUIDE.md
- **Postman Collection:** postman_auth_api_collection.json
- **Project README:** README.md

---

## Quick Reference Commands

```bash
# Build all services
mvn clean package -DskipTests

# Run all services (from IDE, not terminal)
# Or use: bash run-all-services-with-auth.sh

# Send OTP
curl -X POST http://localhost:8083/api/v1/auth/send-otp \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}'

# Verify OTP
curl -X POST http://localhost:8083/api/v1/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","otp":"123456"}'

# Check service health
curl http://localhost:8083/actuator/health

# View Eureka dashboard
open http://localhost:8761
```

---

**Created:** February 27, 2026
**Version:** 1.0.0

