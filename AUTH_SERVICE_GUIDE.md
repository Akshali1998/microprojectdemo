# Authentication & Authorization Implementation Guide

## Overview

This guide explains the complete authentication and authorization system for the e-commerce microservices project using OTP-based login and JWT tokens.

---

## Architecture

### Flow Diagram

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       │ 1. POST /api/v1/auth/send-otp
       ├──────────────────────────────────┐
       │                                  │
       ▼                                  │
┌────────────────────┐                  │
│   Auth Service     │                  │
│  (Port 8083)       │                  │
│                    │                  │
│ • Generate OTP     │                  │
│ • Send via Email   │                  │
│ • Verify OTP       │                  │
│ • Generate JWT     │                  │
│ • Validate Token   │                  │
└────────────┬───────┘                  │
       │                                  │
       │ 2. POST /api/v1/auth/verify-otp │
       │                                  │
       │ 3. Returns JWT Token             │
       │◄─────────────────────────────────┘
       │
       │ 4. Use Token: Authorization: Bearer <token>
       │
       ├─────────────────────────────────────────────────┐
       │                                                 │
       ▼                                                 ▼
┌─────────────────┐                            ┌──────────────────┐
│ Order Service   │                            │ Payment Service  │
│ (Port 8082)     │                            │ (Port 8084)      │
└─────────────────┘                            └──────────────────┘
       │                                                 │
       │ Validate Token with Auth Service               │
       │ Forward User ID in X-User-Id header            │
       │                                                 │
       └─────────────────────────────────────────────────┘
```

---

## Components

### 1. Auth Service (Port 8083)

**Database:** `auth_service_db` (PostgreSQL)

**Tables:**
- `users` - User accounts and roles
- `otps` - One-time passwords with expiration
- `auth_tokens` - JWT tokens with revocation support

### 2. Security Features

- **OTP Generation:** 6-digit random codes with 10-minute expiration
- **Rate Limiting:** Max 5 OTP requests per hour per email
- **JWT Tokens:** HS512 algorithm, 15-minute expiration
- **Token Revocation:** Support for logout and all-device logout
- **CORS:** Enabled for microservice communication

---

## API Endpoints

### 1. Send OTP

**Endpoint:** `POST /api/v1/auth/send-otp`

**Public:** Yes (No authentication required)

**Request:**
```json
{
    "email": "user@example.com"
}
```

**Response (Success - 200):**
```json
{
    "message": "OTP sent successfully to user@example.com",
    "success": true,
    "data": "123456"
}
```

**Response (Error - 400):**
```json
{
    "message": "Failed to send OTP",
    "success": false,
    "error": "Too many OTP requests. Please try again after 1 hour."
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8083/api/v1/auth/send-otp \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com"}'
```

---

### 2. Verify OTP & Login

**Endpoint:** `POST /api/v1/auth/verify-otp`

**Public:** Yes (No authentication required)

**Request:**
```json
{
    "email": "user@example.com",
    "otp": "123456"
}
```

**Response (Success - 200):**
```json
{
    "message": "Login successful",
    "success": true,
    "data": {
        "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
        "userId": "550e8400-e29b-41d4-a716-446655440000",
        "email": "user@example.com",
        "role": "USER",
        "expiresAt": "2026-02-27T14:45:00",
        "message": "Login successful",
        "success": true
    }
}
```

**Response (Error - 401):**
```json
{
    "message": "Login failed",
    "success": false,
    "error": "Invalid or expired OTP"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8083/api/v1/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email":"user@example.com",
    "otp":"123456"
  }'
```

---

### 3. Validate Token

**Endpoint:** `POST /api/v1/auth/validate-token`

**Protected:** Yes (Requires JWT Token)

**Request:**
```
Header: Authorization: Bearer <jwt_token>
```

**Response (Valid - 200):**
```json
{
    "message": "Token is valid",
    "success": true
}
```

**Response (Invalid - 200):**
```json
{
    "message": "Token is invalid",
    "success": false
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8083/api/v1/auth/validate-token \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."
```

---

### 4. Get Current User

**Endpoint:** `GET /api/v1/auth/me`

**Protected:** Yes (Requires JWT Token)

**Response (Success - 200):**
```json
{
    "message": "User information retrieved successfully",
    "success": true,
    "data": {
        "userId": "550e8400-e29b-41d4-a716-446655440000",
        "email": "user@example.com",
        "role": "USER",
        "isActive": true
    }
}
```

**cURL Example:**
```bash
curl -X GET http://localhost:8083/api/v1/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."
```

---

### 5. Logout

**Endpoint:** `POST /api/v1/auth/logout`

**Protected:** Yes (Requires JWT Token)

**Response (Success - 200):**
```json
{
    "message": "Logout successful",
    "success": true
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8083/api/v1/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."
```

---

## Using JWT Token in Other Services

### Step 1: Add Token to Request

All protected API calls in other services should include the JWT token:

```
Authorization: Bearer <jwt_token>
```

### Step 2: Validate Token (Server-Side)

The microservice should validate the token by calling Auth Service:

```bash
POST http://localhost:8083/api/v1/auth/validate-token
Header: Authorization: Bearer <token>
```

### Step 3: Extract User Info from Token

Each microservice can extract user information from the JWT token claims:
- `sub` - User ID
- `email` - User email
- `role` - User role

### Example with Order Service

```bash
# 1. Login first
curl -X POST http://localhost:8083/api/v1/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email":"user@example.com",
    "otp":"123456"
  }'

# Response contains: "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."

# 2. Use token with Order Service
curl -X POST http://localhost:8082/api/v1/orders \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "550e8400-e29b-41d4-a716-446655440000",
    "totalAmount": 99.99,
    "shippingAddress": "123 Main St",
    "billingAddress": "123 Main St",
    "notes": "Express shipping"
  }'
```

---

## Database Setup

### Create Auth Database

```sql
CREATE DATABASE auth_service_db;
\c auth_service_db;

-- Users table (auto-created by JPA)
-- OTPs table (auto-created by JPA)
-- Auth Tokens table (auto-created by JPA)
```

### Application Properties

Edit `auth-service/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://192.168.1.110:5432/auth_service_db
spring.datasource.username=postgres
spring.datasource.password=ind@6117

# JWT Configuration
jwt.secret=ecommerce-microservices-secret-key-change-in-production-with-long-random-string-min-256-bits
jwt.expiration=900000

# OTP Configuration (in milliseconds)
otp.expiration=600000
```

---

## Security Best Practices

### 1. Production Deployment

- **Change JWT Secret:** Use a strong, 256+ bit random key
- **Use HTTPS:** Always use SSL/TLS for token transmission
- **Secure Cookies:** Store tokens in httpOnly, Secure cookies
- **Rate Limiting:** Implement stronger rate limiting on OTP endpoint
- **Token Rotation:** Implement token refresh mechanism

### 2. Token Storage

**Client-Side:**
- **Web:** httpOnly, Secure cookies (recommended)
- **Mobile:** Secure Enclave / Keychain
- **SPA:** LocalStorage only if XSS protection is strong

**Server-Side:**
- Store token in `auth_tokens` table
- Track token usage and revocation
- Implement token blacklist for logout

### 3. API Security

- Validate all user inputs
- Implement request signing
- Add API key authentication for service-to-service calls
- Use API Gateway for rate limiting

### 4. Monitoring

- Log all authentication attempts
- Monitor failed login attempts
- Alert on suspicious patterns
- Audit token usage

---

## Integration with Microservices

### Option 1: Shared JWT Validation (Recommended for MVP)

Each microservice validates tokens independently:

```java
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @PostMapping
    public ResponseEntity<?> createOrder(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody OrderDTO orderDTO) {
        
        // Extract token
        String token = authHeader.substring(7);
        
        // Validate with Auth Service
        boolean isValid = authService.validateToken(token);
        if (!isValid) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        
        // Extract user ID from token
        UUID userId = jwtUtil.getUserIdFromToken(token);
        orderDTO.setCustomerId(userId);
        
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }
}
```

### Option 2: API Gateway Pattern

Use Spring Cloud Gateway to:
- Validate tokens centrally
- Add user headers to requests
- Route to microservices

### Option 3: Service-to-Service Authentication

Use service credentials for internal communication:

```java
@Component
public class ServiceAuthClient {

    public String getServiceToken(String serviceId, String serviceSecret) {
        // Call Auth Service with service credentials
        // Returns token for service-to-service calls
    }
}
```

---

## Testing with Postman

### Collection Setup

1. **Create Environment Variables:**
   - `base_url`: http://localhost:8083
   - `token`: (empty, will be set by test)

2. **Create Requests:**

   **Send OTP:**
   ```
   POST {{base_url}}/api/v1/auth/send-otp
   Body: {"email": "test@example.com"}
   ```

   **Verify OTP:**
   ```
   POST {{base_url}}/api/v1/auth/verify-otp
   Body: {"email": "test@example.com", "otp": "123456"}
   
   Pre-request Script:
   // Use OTP from Send OTP response
   ```

   **Use Token:**
   ```
   GET {{base_url}}/api/v1/auth/me
   Header: Authorization: Bearer {{token}}
   ```

---

## Troubleshooting

### OTP Not Received

- Check notification service is running
- Verify email configuration in application.properties
- Check service logs for email sending errors

### Token Validation Fails

- Verify JWT secret matches across services
- Check token expiration time
- Validate token format (Bearer <token>)

### Port Already in Use

```bash
# Kill process on port 8083
lsof -ti:8083 | xargs kill -9
```

---

## Next Steps

1. **Phase 2:** Implement OAuth2/OpenID Connect
2. **Phase 3:** Add role-based access control (RBAC)
3. **Phase 4:** Implement multi-factor authentication (MFA)
4. **Phase 5:** Add social login (Google, GitHub, etc.)
5. **Phase 6:** Implement token refresh mechanism

---

## Contact & Support

For questions or issues, refer to the main README.md in the project root.

