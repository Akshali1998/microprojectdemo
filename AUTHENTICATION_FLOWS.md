# Authentication Flow Diagrams & Examples

## 1. Complete OTP Login Flow

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          CLIENT APPLICATION                             │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                    ┌───────────────┼───────────────┐
                    │               │               │
                    ▼               ▼               ▼
        ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
        │  STEP 1: Enter  │  │  STEP 2: Enter  │  │  STEP 3: Use    │
        │  Email Address  │  │  OTP Code       │  │  JWT Token      │
        └────────┬────────┘  └────────┬────────┘  └────────┬────────┘
                 │                    │                     │
                 │                    │                     │
        ┌────────▼────────────────────▼─────────────────────▼─────────┐
        │              HTTP REQUEST TO AUTH SERVICE                    │
        │                    (Port 8083)                               │
        └─────────────────────────────────────────────────────────────┘
                 │                    │                     │
    ┌────────────▼────────┐  ┌────────▼────────┐  ┌────────▼────────┐
    │ POST /send-otp      │  │POST /verify-otp │  │  Add Header     │
    │ {"email":"..."}     │  │ {"email":"..."} │  │Authorization:   │
    │                     │  │  "otp":"123456"}│  │Bearer <token>   │
    └────────┬────────────┘  └────────┬────────┘  └────────┬────────┘
             │                        │                     │
             ▼                        ▼                     ▼
    ┌─────────────────────────────────────────────────────────────────┐
    │                   AUTH SERVICE DATABASE                         │
    │                                                                 │
    │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
    │  │    Users     │  │     OTPs     │  │Auth_Tokens  │         │
    │  │ • user_id    │  │ • otp_id     │  │• token_id   │         │
    │  │ • email      │  │ • email      │  │ • user_id   │         │
    │  │ • role       │  │ • code       │  │ • token(JWT)│         │
    │  │ • is_active  │  │ • expires_at │  │ • expires_at│         │
    │  └──────────────┘  └──────────────┘  └──────────────┘         │
    │                                                                 │
    └────────────────────────────────────────────────────────────────┘
             │                        │                     │
    ┌────────▼────────────┐  ┌────────▼──────────────┐    │
    │ RESPONSE:           │  │ RESPONSE:            │    │
    │ {"success": true,   │  │ {"success": true,    │    │
    │  "message": "OTP    │  │  "message": "Login   │    │
    │   sent",            │  │   successful",       │    │
    │  "data": "123456"}  │  │  "data": {           │    │
    │                     │  │   "token": "jwt...", │    │
    │                     │  │   "userId": "...",   │    │
    │                     │  │   "role": "USER"     │    │
    │                     │  │  }}                  │    │
    └─────────────────────┘  └──────────────────────┘    │
                                                          │
                                              ┌───────────▼──────────┐
                                              │ SEND TO ORDER SERVICE│
                                              │ (Port 8082)          │
                                              │ POST /orders         │
                                              │ Header: Auth Token   │
                                              └──────────────────────┘
```

---

## 2. Sequence Diagram: User Login

```
User              Client          Auth Service       Database
│                  │                   │                │
├─ Email ────────> │                   │                │
│                  │ POST /send-otp    │                │
│                  ├─────────────────> │ Generate OTP   │
│                  │                   ├───────────────>│
│                  │                   │ Store OTP      │
│                  │ OTP Code Response │                │
│                  │ <─────────────────┤ <─────────────┤
├─ Display OTP ─<─ │                   │                │
│                  │                   │                │
├─ OTP ─────────> │                   │                │
│                  │ POST /verify-otp  │                │
│                  ├─────────────────> │ Validate OTP   │
│                  │                   ├───────────────>│
│                  │                   │ Mark Used      │
│                  │                   │ Create JWT     │
│                  │                   ├───────────────>│
│                  │ JWT Token         │ Save Token     │
│                  │ <─────────────────┤ <─────────────┤
├─ Store Token ─<─ │                   │                │
│                  │                   │                │
│ (Now Authorized) │                   │                │
│                  │ GET /orders       │                │
│                  │ Header: Bearer JWT├─────────────> │
│                  │                   │ Validate Token │
│                  │ <─────────────────┤ <─────────────┤
├─ Orders ──────<─ │                   │                │
│                  │                   │                │
```

---

## 3. Microservice Communication with JWT

```
┌─────────────────────────────────────────────────────────────────┐
│                     CLIENT                                       │
│              1. Login with OTP to Auth Service                   │
│              2. Receive JWT Token                                │
└─────────────────────┬───────────────────────────────────────────┘
                      │
                      │ Authorization: Bearer <JWT>
                      │
        ┌─────────────▼───────────────┐
        │      ORDER SERVICE          │
        │      (Port 8082)            │
        │                             │
        │  1. Extract JWT from Header │
        │  2. Validate Token          │
        │  3. Extract Claims:         │
        │     - userId (from sub)     │
        │     - email (from email)    │
        │     - role (from role)      │
        │                             │
        │  4. Add to Request:         │
        │     X-User-Id: <userId>    │
        │     X-User-Email: <email>  │
        └─────────────┬───────────────┘
                      │
        ┌─────────────▼──────────────┐
        │  INVENTORY SERVICE         │
        │  (Port 8085)               │
        │                            │
        │ Uses X-User-Id header      │
        │ for auditing & logging     │
        └────────────────────────────┘
                      │
        ┌─────────────▼──────────────┐
        │  PAYMENT SERVICE           │
        │  (Port 8084)               │
        │                            │
        │ Uses X-User-Id header      │
        │ for transaction tracking   │
        └────────────────────────────┘
```

---

## 4. Real-World Test Scenario

### Scenario: User Places an Order

**Step 1: Send OTP**
```bash
curl -X POST http://localhost:8083/api/v1/auth/send-otp \
  -H "Content-Type: application/json" \
  -d '{"email":"john.doe@example.com"}'

# Response:
# {
#   "message": "OTP sent successfully to john.doe@example.com",
#   "success": true,
#   "data": "456789"  // OTP for testing
# }
```

**Step 2: Verify OTP & Get Token**
```bash
curl -X POST http://localhost:8083/api/v1/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email":"john.doe@example.com",
    "otp":"456789"
  }'

# Response:
# {
#   "message": "Login successful",
#   "success": true,
#   "data": {
#     "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1NTBlODQwMC1lMjliLTQxZDQtYTcxNi00NDY2NTU0NDAwMDAiLCJlbWFpbCI6ImpvaG4uZG9lQGV4YW1wbGUuY29tIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3MDkwMzM0MDAsImV4cCI6MTcwOTAzNDMwMH0.abcdefg...",
#     "userId": "550e8400-e29b-41d4-a716-446655440000",
#     "email": "john.doe@example.com",
#     "role": "USER",
#     "expiresAt": "2026-02-27T14:15:00"
#   }
# }
```

**Step 3: Create Order with Token**
```bash
TOKEN="eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."

curl -X POST http://localhost:8082/api/v1/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "550e8400-e29b-41d4-a716-446655440000",
    "totalAmount": 299.99,
    "shippingAddress": "123 Oak Street, New York, NY 10001",
    "billingAddress": "123 Oak Street, New York, NY 10001",
    "notes": "Gift wrapping requested"
  }'

# Response:
# {
#   "orderId": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
#   "customerId": "550e8400-e29b-41d4-a716-446655440000",
#   "totalAmount": 299.99,
#   "status": "PENDING",
#   "shippingAddress": "123 Oak Street, New York, NY 10001",
#   "billingAddress": "123 Oak Street, New York, NY 10001",
#   "createdAt": "2026-02-27T14:00:00",
#   "notes": "Gift wrapping requested"
# }
```

**Step 4: Track User Activity**

Server-side logs show:
```
[Auth Service] ✓ OTP sent to john.doe@example.com
[Auth Service] ✓ OTP verified for john.doe@example.com
[Auth Service] ✓ JWT token issued for user: 550e8400-e29b-41d4-a716-446655440000
[Order Service] ✓ Order created by user: 550e8400-e29b-41d4-a716-446655440000
[Order Service] X-User-Email: john.doe@example.com
[Order Service] X-User-Id: 550e8400-e29b-41d4-a716-446655440000
```

---

## 5. Error Scenarios

### Scenario A: Invalid OTP

```bash
# Request with wrong OTP
curl -X POST http://localhost:8083/api/v1/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email":"john.doe@example.com",
    "otp":"999999"
  }'

# Response (401 Unauthorized):
# {
#   "message": "Login failed",
#   "success": false,
#   "error": "Invalid or expired OTP"
# }
```

### Scenario B: Expired OTP

```bash
# Request after 10 minutes
curl -X POST http://localhost:8083/api/v1/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email":"john.doe@example.com",
    "otp":"456789"
  }'

# Response (401 Unauthorized):
# {
#   "message": "Login failed",
#   "success": false,
#   "error": "Invalid or expired OTP"
# }
```

### Scenario C: Rate Limit Exceeded

```bash
# After 5 OTP requests in 1 hour
curl -X POST http://localhost:8083/api/v1/auth/send-otp \
  -H "Content-Type: application/json" \
  -d '{"email":"john.doe@example.com"}'

# Response (400 Bad Request):
# {
#   "message": "Failed to send OTP",
#   "success": false,
#   "error": "Too many OTP requests. Please try again after 1 hour."
# }
```

### Scenario D: Invalid Token Format

```bash
# Missing Bearer prefix
curl -X GET http://localhost:8083/api/v1/auth/me \
  -H "Authorization: xyz123token"

# Response (400 Bad Request):
# {
#   "message": "Invalid token format",
#   "success": false,
#   "error": "Authorization header must be in format: Bearer <token>"
# }
```

### Scenario E: Expired Token

```bash
# Token expired (after 15 minutes)
curl -X GET http://localhost:8083/api/v1/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."

# Response (401 Unauthorized):
# {
#   "message": "Failed to retrieve user information",
#   "success": false,
#   "error": "JWT validation failed"
# }
```

---

## 6. JWT Token Breakdown

### Example Token
```
eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiI1NTBlODQwMC1lMjliLTQxZDQtYTcxNi00NDY2NTU0NDAwMDAiLCJlbWFpbCI6ImpvaG4uZG9lQGV4YW1wbGUuY29tIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3MDkwMzM0MDAsImV4cCI6MTcwOTAzNDMwMH0.
abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+/
```

### Decoded Header
```json
{
  "alg": "HS512",
  "typ": "JWT"
}
```

### Decoded Payload
```json
{
  "sub": "550e8400-e29b-41d4-a716-446655440000",
  "email": "john.doe@example.com",
  "role": "USER",
  "iat": 1709033400,
  "exp": 1709034300
}
```

**Key Claims:**
- `sub` (Subject): User ID
- `email`: User email
- `role`: User role
- `iat` (Issued At): Token creation timestamp
- `exp` (Expiration): Token expiration timestamp (15 minutes)

---

## 7. Security Considerations

### Token Security

✅ **Secure:**
- Use HTTPS/TLS for transmission
- Store in httpOnly cookies (web)
- Use Keychain/Secure Enclave (mobile)
- Never log full tokens

❌ **Insecure:**
- Store in LocalStorage (XSS vulnerable)
- Send via GET parameters
- Hardcode secrets in source code
- Log full tokens

### OTP Security

✅ **Good Practices:**
- 6-digit random codes
- 10-minute expiration
- Rate limiting (5 requests/hour)
- Mark as used after verification
- No OTP value in logs

### Database Security

✅ **Recommended:**
- Enable SSL for DB connections
- Use strong passwords
- Encrypt sensitive data
- Regular backups
- Access controls

---

## 8. Monitoring & Debugging

### Check Service Status

```bash
# Check Auth Service
curl http://localhost:8083/actuator/health

# Check Order Service
curl http://localhost:8082/actuator/health

# Check Eureka
curl http://localhost:8761/eureka/apps
```

### View Logs

```bash
# Auth Service logs
tail -f service-logs/auth-service.log

# Order Service logs
tail -f service-logs/order-service.log

# Search for errors
grep "ERROR" service-logs/*.log

# Search for specific user
grep "john.doe@example.com" service-logs/auth-service.log
```

### Debug JWT Token

```bash
# Copy token and decode at jwt.io
# Or use Python:
python3 << 'EOF'
import json
import base64

token = "YOUR_JWT_TOKEN_HERE"
parts = token.split('.')

# Decode payload (add padding if needed)
payload = parts[1]
payload += '=' * (4 - len(payload) % 4)
decoded = base64.urlsafe_b64decode(payload)
print(json.dumps(json.loads(decoded), indent=2))
EOF
```

---

## 9. Performance Tuning

### Database Optimization

```sql
-- Create indexes for faster queries
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_otps_email_used ON otps(email, is_used);
CREATE INDEX idx_auth_tokens_user_id ON auth_tokens(user_id);
```

### Application Configuration

```properties
# Connection pool
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2

# JWT caching (optional)
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=15m
```

---

## Summary

**Authentication Flow:**
1. User sends email → Auth Service generates OTP
2. User sends OTP → Auth Service issues JWT
3. User sends JWT with requests → Microservices validate token
4. Token claims identify user → Services log audit trail

**Security:** OTP (10 min) + JWT (15 min) + Rate Limiting + Token Revocation

**Integration:** All microservices validate same JWT secret

---

**For more details, see:** AUTH_SERVICE_GUIDE.md & IMPLEMENTATION_GUIDE.md

