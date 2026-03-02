# 📋 Swagger/OpenAPI Quick Reference Card

## 🎯 What's Available Now

```
✅ Swagger UI (Interactive browser testing)
✅ OpenAPI JSON specs (API documentation export)
✅ Auto-discovered endpoints (No manual documentation needed)
✅ Example curl commands (Ready to copy-paste)
✅ Postman integration (Import and test)
✅ Startup script (One-command launch)
```

---

## 🌐 Access URLs (After services start)

```
Inventory Service (8080)    | http://localhost:8080/swagger-ui/index.html
Order Service (8082)        | http://localhost:8082/swagger-ui/index.html
Payment Service (8081)      | http://localhost:8081/swagger-ui/index.html
Notification Service (8090) | http://localhost:8090/swagger-ui/index.html
Eureka Dashboard (8761)     | http://localhost:8761/
```

---

## 🚀 Fastest Way to Start

### Copy-Paste This:
```bash
cd /home/developer/SOFTWARE/ecommerce-microservices
chmod +x start-services-with-swagger.sh
./start-services-with-swagger.sh
```

Then open: **http://localhost:8080/swagger-ui/index.html**

---

## 📄 Documentation Files

| File | What to Use It For |
|------|-------------------|
| `SWAGGER_SUMMARY.md` | **👈 [START HERE]** Overview & next steps |
| `API_ENDPOINTS.md` | All endpoints, DTOs, request/response examples |
| `QUICK_API_TESTING.md` | Copy-paste curl commands & testing workflows |
| `SWAGGER_OPENAPI_SETUP.md` | Setup, verification, troubleshooting |

---

## 🔍 Quick Endpoint Reference

### Inventory (Port 8080)
```
POST   /api/v1/inventory                              Create inventory
GET    /api/v1/inventory                              Get all
GET    /api/v1/inventory/{id}                         Get by ID
GET    /api/v1/inventory/product/{productId}          Get by product
POST   /api/v1/inventory/add/{productId}?quantity=n   Add stock
POST   /api/v1/inventory/reserve/{productId}?qty=n    Reserve stock
POST   /api/v1/inventory/release/{productId}?qty=n    Release stock
GET    /api/v1/inventory/low-stock                    Low stock items
GET    /api/v1/inventory/warehouse/{wh}               Get by warehouse
GET    /api/v1/inventory/stats/low-stock-count        Count low stock
```

### Order (Port 8082)
```
POST   /api/v1/orders                                 Create order
GET    /api/v1/orders                                 Get all
GET    /api/v1/orders/{id}                            Get by ID
GET    /api/v1/orders/customer/{customerId}           Get by customer
GET    /api/v1/orders/status/{status}                 Get by status
PATCH  /api/v1/orders/{id}/status?status=X            Update status
PUT    /api/v1/orders/{id}                            Update order
DELETE /api/v1/orders/{id}                            Delete order
GET    /api/v1/orders/stats/status/{status}           Count by status
```

### Payment (Port 8081)
```
POST   /api/v1/payments                               Create payment
GET    /api/v1/payments                               Get all
GET    /api/v1/payments/{id}                          Get by ID
GET    /api/v1/payments/order/{orderId}               Get by order
GET    /api/v1/payments/customer/{customerId}         Get by customer
GET    /api/v1/payments/status/{status}               Get by status
POST   /api/v1/payments/{id}/process?method=X         Process payment
PATCH  /api/v1/payments/{id}/status?status=X          Update status
DELETE /api/v1/payments/{id}                          Delete payment
GET    /api/v1/payments/stats/status/{status}         Count by status
```

### Notification (Port 8090)
```
POST   /api/v1/notify/send                            Send notification
```

---

## 💡 Testing Methods (In Order of Ease)

### 1️⃣ Easiest: Swagger UI (Browser)
```
1. Open: http://localhost:8080/swagger-ui/index.html
2. Find endpoint
3. Click "Try it out"
4. Fill in values
5. Click "Execute"
6. See response
```

### 2️⃣ Easy: curl (Terminal)
```bash
# Copy from QUICK_API_TESTING.md
curl -X POST http://localhost:8080/api/v1/inventory \
  -H "Content-Type: application/json" \
  -d '{"productId":"SKU","productName":"Name","availableQuantity":100,...}' | jq .
```

### 3️⃣ Professional: Postman (Desktop App)
```
1. Open Postman
2. Click Import → Link
3. Paste: http://localhost:8080/v3/api-docs
4. Click Continue → Import
5. Use pre-made requests
```

### 4️⃣ Developer: IDE (IntelliJ)
```
1. Right-click main class → Run
2. Servers start with hot-reload
3. Open Swagger UI
4. Code changes auto-picked-up
```

---

## 🎁 What's Included

### Code Changes
- ✅ 4 × pom.xml files (springdoc dependency)
- ✅ 4 × OpenApiConfig.java (Spring config beans)

### Documentation
- ✅ API_ENDPOINTS.md (All endpoints + examples)
- ✅ SWAGGER_OPENAPI_SETUP.md (Setup guide)
- ✅ QUICK_API_TESTING.md (30+ curl examples)
- ✅ SWAGGER_SUMMARY.md (This file)

### Tools
- ✅ start-services-with-swagger.sh (One-command startup)

---

## ⚡ Common Tasks

### Create Inventory
```bash
curl -X POST http://localhost:8080/api/v1/inventory \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "SKU-001",
    "productName": "Product",
    "availableQuantity": 100,
    "reorderLevel": 20,
    "warehouse": "WH-1"
  }' | jq .
```

### Add Stock
```bash
curl -X POST "http://localhost:8080/api/v1/inventory/add/SKU-001?quantity=50"
```

### Reserve Stock
```bash
curl -X POST "http://localhost:8080/api/v1/inventory/reserve/SKU-001?quantity=10"
```

### Get All Inventory
```bash
curl http://localhost:8080/api/v1/inventory | jq .
```

### Create Order
```bash
curl -X POST http://localhost:8082/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-123",
    "totalAmount": 999.99,
    "status": "PENDING",
    "shippingAddress": "123 Main",
    "billingAddress": "123 Main"
  }' | jq .
```

### Create Payment
```bash
curl -X POST http://localhost:8081/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "order-uuid",
    "customerId": "cust-uuid",
    "amount": 999.99,
    "paymentMethod": "CARD"
  }' | jq .
```

### Send Notification
```bash
curl -X POST http://localhost:8090/api/v1/notify/send \
  -H "Content-Type: application/json" \
  -d '{
    "to": "user@example.com",
    "subject": "Order Confirmed",
    "body": "Your order has been confirmed"
  }' | jq .
```

---

## 🔧 Manual Build & Start

```bash
# Build all services
mvn clean package -DskipTests

# Terminal 1: Eureka (wait 5 seconds)
java -jar eureka-server/target/eureka-server-1.0.0.jar

# Terminal 2: Inventory
java -jar inventory-service/target/inventory-service-1.0.0.jar

# Terminal 3: Order
java -jar order-service/target/order-service-1.0.0.jar

# Terminal 4: Payment
java -jar payment-service/target/payment-service-1.0.0.jar

# Terminal 5: Notification (optional)
java -jar notification-service/target/notification-service-1.0.0.jar
```

---

## ✅ Pre-Flight Checklist

- [ ] Java 17+ installed
- [ ] PostgreSQL running (for inventory/order/payment - if using DB)
- [ ] Ports 8080, 8081, 8082, 8090, 8761 free
- [ ] Read `API_ENDPOINTS.md` for endpoint info
- [ ] Build with `mvn clean package -DskipTests`
- [ ] Start Eureka first, wait 5 sec
- [ ] Start other services
- [ ] Open Swagger UI in browser

---

## 🆘 Quick Troubleshoot

```
❌ Build fails: Java version? Try: java -version
❌ Port in use: Kill process or change server.port in application.properties
❌ 404 on Swagger: Service not started? Check logs.
❌ Can't reach DB: Postgres running? Check credentials in application.properties
❌ Won't register Eureka: Start Eureka first! Wait 10 sec.
```

---

## 📊 Response Format

All Inventory endpoints return:
```json
{
  "message": "Success message",
  "status": "OK" or "ERROR",
  "statusCode": 200,
  "data": { /* payload */ },
  "error": null,
  "exception": "NA"
}
```

Order/Payment endpoints return the DTO directly (standard REST).

---

## 🎯 One-Liner Summary

**Swagger/OpenAPI is now enabled on all 4 microservices. Start with `./start-services-with-swagger.sh` then open http://localhost:8080/swagger-ui/index.html to test endpoints interactively.**

---

## 📞 Need Help?

- **Swagger not loading?** → See SWAGGER_OPENAPI_SETUP.md
- **What endpoints exist?** → See API_ENDPOINTS.md
- **Want curl examples?** → See QUICK_API_TESTING.md
- **General questions?** → See SWAGGER_SUMMARY.md

---

**You're all set! 🚀 Open Swagger UI and start testing.**

