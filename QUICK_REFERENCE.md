# 🚀 Quick Reference Card - Authentication System

## One-Page Cheat Sheet

### Services Overview
```
┌─────────────────────────────────────────────────────┐
│ EUREKA SERVER (Port 8761)                          │
│ Service Discovery & Registration                    │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│ AUTH SERVICE (Port 8083) ⭐ NEW                    │
│ • OTP Generation/Verification                       │
│ • JWT Token Management                              │
│ • User Management                                   │
│ • Token Validation                                  │
└─────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────────┐
│ MICROSERVICES (with JWT validation)                           │
├─────────────────────────────────────────────────────────────┤
│ Order Service (8082) | Payment Service (8084)                │
│ Inventory Service (8085) | Notification Service (8090)       │
└───────────────────────────────────────────────────────────────┘
```

---

## Quick API Reference

### 1. Send OTP
```
POST /api/v1/auth/send-otp
Content-Type: application/json

{"email":"user@example.com"}

RESPONSE: {"success":true, "data":"123456"}
```

### 2. Verify OTP → Get Token
```
POST /api/v1/auth/verify-otp
Content-Type: application/json

{"email":"user@example.com", "otp":"123456"}

RESPONSE: {"success":true, "data":{"token":"jwt..."}}
```

### 3. Use Token with Services
```
GET /api/v1/orders
Authorization: Bearer <token_from_step_2>

All requests to protected endpoints require this header
```

### 4. Validate Token
```
POST /api/v1/auth/validate-token
Authorization: Bearer <token>

RESPONSE: {"success":true, "message":"Token is valid"}
```

### 5. Get Current User
```
GET /api/v1/auth/me
Authorization: Bearer <token>

RESPONSE: {"success":true, "data":{"userId":"...", "email":"..."}}
```

### 6. Logout
```
POST /api/v1/auth/logout
Authorization: Bearer <token>

RESPONSE: {"success":true, "message":"Logout successful"}
```

---

## 🛠️ Setup (Copy-Paste)

```bash
# 1. Create database
psql -h 192.168.1.110 -U postgres -c "CREATE DATABASE auth_service_db;"

# 2. Build
cd /home/developer/SOFTWARE/ecommerce-microservices
mvn clean package -DskipTests

# 3. Run from IDE (Run button in IntelliJ)
# - eureka-server/.../EurekaServerApplication
# - auth-service/.../AuthServiceApplication  
# - order-service/.../OrderServiceApplication
# - (etc for other services)

# 4. Test
curl -X POST http://localhost:8083/api/v1/auth/send-otp \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}'
```

---

## 📊 JWT Token Claims

```
Payload in token:
{
  "sub": "user-id-uuid",
  "email": "user@example.com",
  "role": "USER",
  "iat": 1709033400,      // Issued at
  "exp": 1709034300       // Expires (15 minutes later)
}
```

**Extract from token:**
- `sub` → User ID
- `email` → User email
- `role` → User role

---

## 📁 Key Files

```
auth-service/pom.xml                              • Add JWT deps
auth-service/.../AuthServiceApplication.java      • Start here
auth-service/.../AuthController.java              • 5 API endpoints
auth-service/.../JwtUtil.java                     • JWT generation/validation
auth-service/.../application.properties           • DB & JWT config
```

---

## ⚙️ Configuration

**Edit:** `auth-service/src/main/resources/application.properties`

```properties
# Database (create this DB first!)
spring.datasource.url=jdbc:postgresql://192.168.1.110:5432/auth_service_db

# JWT Secret (CHANGE IN PRODUCTION!)
jwt.secret=ecommerce-microservices-secret-key-...

# OTP Expiry (10 minutes)
otp.expiration=600000

# JWT Expiry (15 minutes)
jwt.expiration=900000
```

---

## 🔐 Security Summary

| Feature | Details |
|---------|---------|
| **OTP** | 6 digits, 10 min expiry, 5/hour rate limit |
| **JWT** | HS512 algorithm, 15 min expiry |
| **Password** | BCrypt hashing (extensible) |
| **CORS** | Enabled for microservices |
| **Revocation** | Token blacklisting on logout |
| **Database** | PostgreSQL with 3 tables |

---

## 🧪 Quick Test (Curl)

```bash
# Step 1: Send OTP
curl -X POST http://localhost:8083/api/v1/auth/send-otp \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}'
# Copy OTP from response

# Step 2: Login
TOKEN=$(curl -s -X POST http://localhost:8083/api/v1/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","otp":"<paste_otp>"}' | jq -r '.data.token')

# Step 3: Use token
curl -X GET http://localhost:8083/api/v1/auth/me \
  -H "Authorization: Bearer $TOKEN"

# Step 4: Logout
curl -X POST http://localhost:8083/api/v1/auth/logout \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📍 Endpoints at a Glance

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| POST | `/send-otp` | ❌ | Request OTP |
| POST | `/verify-otp` | ❌ | Login, get token |
| POST | `/validate-token` | ✅ | Verify JWT |
| GET | `/me` | ✅ | Current user |
| POST | `/logout` | ✅ | Revoke token |

---

## 🌐 Service URLs

```
Eureka Dashboard:      http://localhost:8761
Auth Service:          http://localhost:8083
Order Service:         http://localhost:8082
Payment Service:       http://localhost:8084
Inventory Service:     http://localhost:8085
Notification Service:  http://localhost:8090

API Docs (Swagger):    http://localhost:8083/api/v1/auth/swagger-ui.html
```

---

## ⚠️ Common Issues

| Problem | Solution |
|---------|----------|
| Port 8083 in use | `lsof -ti:8083 \| xargs kill -9` |
| Database not found | Run: `CREATE DATABASE auth_service_db;` |
| OTP not working | Check DB connection, review logs |
| Token invalid | Ensure jwt.secret matches across services |
| Service not starting | Check logs: `tail -f service-logs/auth-service.log` |

---

## 🔄 Complete Flow (5 Steps)

```
1️⃣  POST /send-otp
    {"email":"user@example.com"}
    ↓
2️⃣  Receive OTP (e.g., "123456")
    ↓
3️⃣  POST /verify-otp
    {"email":"user@example.com", "otp":"123456"}
    ↓
4️⃣  Receive JWT Token
    ↓
5️⃣  Add to requests: Authorization: Bearer <token>
```

---

## 📚 Documentation Files

| File | Content |
|------|---------|
| **AUTH_SERVICE_GUIDE.md** | Complete API documentation |
| **IMPLEMENTATION_GUIDE.md** | Setup & integration |
| **AUTHENTICATION_FLOWS.md** | Visual flows & examples |
| **AUTHENTICATION_SUMMARY.md** | This complete guide |
| **postman_auth_api_collection.json** | Postman tests |

---

## ✅ Verification Checklist

- [ ] PostgreSQL running
- [ ] `auth_service_db` created
- [ ] Eureka started (8761)
- [ ] Auth Service started (8083)
- [ ] Other services started
- [ ] OTP endpoint works
- [ ] Token received
- [ ] Token validates
- [ ] Logout works
- [ ] Order Service accepts token

---

## 💡 Pro Tips

1. **Testing**: Use Postman collection for easier testing
2. **Debugging**: Check `service-logs/*.log` for errors
3. **Development**: OTP returned in response (remove in production)
4. **Security**: Change `jwt.secret` before going to production
5. **Monitoring**: Enable debug logs for JWT issues: `logging.level.com.ecommerce=DEBUG`

---

## 🚀 Production Deployment

```properties
# BEFORE DEPLOYING:

# 1. Change secret (minimum 256 bits)
jwt.secret=YOUR_LONG_RANDOM_SECRET_HERE

# 2. Enable HTTPS
server.ssl.enabled=true

# 3. Use environment variables
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}

# 4. Enable production logging
logging.level.org.springframework.security=INFO

# 5. Set strict CORS
cors.allowed-origins=https://yourdomain.com
```

---

## 🎯 Key Numbers

| Item | Value |
|------|-------|
| Auth Service Port | 8083 |
| OTP Expiry | 10 minutes |
| JWT Expiry | 15 minutes |
| OTP Rate Limit | 5/hour per email |
| OTP Length | 6 digits |
| JWT Algorithm | HS512 |
| Database | PostgreSQL |

---

## 📞 Quick Links

- **API Docs**: http://localhost:8083/api/v1/auth/swagger-ui.html
- **Eureka**: http://localhost:8761
- **Health Check**: http://localhost:8083/actuator/health
- **Database**: 192.168.1.110:5432

---

## 🎉 You're All Set!

```
Auth Service:      ✅ Ready
JWT Tokens:        ✅ Ready
Microservices:     ✅ Ready
Documentation:     ✅ Ready
Testing:           ✅ Ready

→ Start services and test!
```

---

**For detailed info**, refer to:
- Full API Guide: `AUTH_SERVICE_GUIDE.md`
- Setup Steps: `IMPLEMENTATION_GUIDE.md`
- Flow Diagrams: `AUTHENTICATION_FLOWS.md`

---

**Last Updated:** February 27, 2026  
**Version:** 1.0.0

