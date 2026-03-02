# Quick API Testing Guide with Swagger & Postman

This guide provides quick copy-paste commands and examples for testing all microservice endpoints.

---

## Quick Start (One Command)

```bash
cd /home/developer/SOFTWARE/ecommerce-microservices
chmod +x start-services-with-swagger.sh
./start-services-with-swagger.sh
```

Then open in browser:
- **Inventory:** http://localhost:8080/swagger-ui/index.html
- **Order:** http://localhost:8082/swagger-ui/index.html
- **Payment:** http://localhost:8081/swagger-ui/index.html
- **Notification:** http://localhost:8090/swagger-ui/index.html

---

## Manual Build & Start

```bash
# Build
cd /home/developer/SOFTWARE/ecommerce-microservices
mvn clean package -DskipTests

# Terminal 1 - Start Eureka (wait 5 sec)
java -jar eureka-server/target/eureka-server-1.0.0.jar

# Terminal 2 - Inventory Service
java -jar inventory-service/target/inventory-service-1.0.0.jar

# Terminal 3 - Order Service
java -jar order-service/target/order-service-1.0.0.jar

# Terminal 4 - Payment Service
java -jar payment-service/target/payment-service-1.0.0.jar

# Terminal 5 (optional) - Notification Service
java -jar notification-service/target/notification-service-1.0.0.jar
```

---

## Swagger UI Testing (Browser)

### Easiest Method: Use Swagger UI
1. Open: http://localhost:8080/swagger-ui/index.html
2. Find endpoint (e.g., "POST /api/v1/inventory")
3. Click **"Try it out"**
4. Fill in request body (for POST/PUT)
5. Click **"Execute"**
6. See response below

---

## curl Testing (Terminal)

### Inventory Service Examples

**1. Create Inventory**
```bash
curl -X POST http://localhost:8080/api/v1/inventory \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "SKU-001",
    "productName": "Product 1",
    "availableQuantity": 100,
    "reorderLevel": 20,
    "warehouse": "WH-1"
  }' | jq .
```

**2. Get All Inventory**
```bash
curl http://localhost:8080/api/v1/inventory | jq .
```

**3. Get Inventory by ID** (replace UUID)
```bash
curl http://localhost:8080/api/v1/inventory/550e8400-e29b-41d4-a716-446655440000 | jq .
```

**4. Get by Product ID**
```bash
curl http://localhost:8080/api/v1/inventory/product/SKU-001 | jq .
```

**5. Add Stock**
```bash
curl -X POST "http://localhost:8080/api/v1/inventory/add/SKU-001?quantity=50" | jq .
```

**6. Reserve Stock**
```bash
curl -X POST "http://localhost:8080/api/v1/inventory/reserve/SKU-001?quantity=10" | jq .
```

**7. Release Stock**
```bash
curl -X POST "http://localhost:8080/api/v1/inventory/release/SKU-001?quantity=5" | jq .
```

**8. Get Low Stock Items**
```bash
curl http://localhost:8080/api/v1/inventory/low-stock | jq .
```

**9. Get by Warehouse**
```bash
curl http://localhost:8080/api/v1/inventory/warehouse/WH-1 | jq .
```

**10. Low Stock Count**
```bash
curl http://localhost:8080/api/v1/inventory/stats/low-stock-count | jq .
```

### Order Service Examples

**1. Create Order**
```bash
curl -X POST http://localhost:8082/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "550e8400-e29b-41d4-a716-446655440001",
    "totalAmount": 999.99,
    "status": "PENDING",
    "shippingAddress": "123 Main St",
    "billingAddress": "123 Main St",
    "notes": "Rush delivery"
  }' | jq .
```

**2. Get All Orders**
```bash
curl http://localhost:8082/api/v1/orders | jq .
```

**3. Get Order by ID**
```bash
curl http://localhost:8082/api/v1/orders/550e8400-e29b-41d4-a716-446655440002 | jq .
```

**4. Get Orders by Customer**
```bash
curl http://localhost:8082/api/v1/orders/customer/550e8400-e29b-41d4-a716-446655440001 | jq .
```

**5. Get Orders by Status**
```bash
curl http://localhost:8082/api/v1/orders/status/PENDING | jq .
```

**6. Update Order Status**
```bash
curl -X PATCH "http://localhost:8082/api/v1/orders/550e8400-e29b-41d4-a716-446655440002/status?status=SHIPPED" | jq .
```

**7. Delete Order**
```bash
curl -X DELETE http://localhost:8082/api/v1/orders/550e8400-e29b-41d4-a716-446655440002 | jq .
```

**8. Get Total Orders by Status**
```bash
curl http://localhost:8082/api/v1/orders/stats/status/PENDING | jq .
```

### Payment Service Examples

**1. Create Payment**
```bash
curl -X POST http://localhost:8081/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "550e8400-e29b-41d4-a716-446655440002",
    "customerId": "550e8400-e29b-41d4-a716-446655440001",
    "amount": 999.99,
    "paymentMethod": "CARD",
    "status": "PENDING",
    "remarks": "Payment for order 123"
  }' | jq .
```

**2. Get All Payments**
```bash
curl http://localhost:8081/api/v1/payments | jq .
```

**3. Get Payment by ID**
```bash
curl http://localhost:8081/api/v1/payments/550e8400-e29b-41d4-a716-446655440003 | jq .
```

**4. Get Payment by Order ID**
```bash
curl http://localhost:8081/api/v1/payments/order/550e8400-e29b-41d4-a716-446655440002 | jq .
```

**5. Get Payments by Customer**
```bash
curl http://localhost:8081/api/v1/payments/customer/550e8400-e29b-41d4-a716-446655440001 | jq .
```

**6. Get Payments by Status**
```bash
curl http://localhost:8081/api/v1/payments/status/PENDING | jq .
```

**7. Process Payment**
```bash
curl -X POST "http://localhost:8081/api/v1/payments/550e8400-e29b-41d4-a716-446655440003/process?paymentMethod=card" | jq .
```

**8. Update Payment Status**
```bash
curl -X PATCH "http://localhost:8081/api/v1/payments/550e8400-e29b-41d4-a716-446655440003/status?status=SUCCESS" | jq .
```

**9. Delete Payment**
```bash
curl -X DELETE http://localhost:8081/api/v1/payments/550e8400-e29b-41d4-a716-446655440003 | jq .
```

**10. Get Total Payments by Status**
```bash
curl http://localhost:8081/api/v1/payments/stats/status/SUCCESS | jq .
```

### Notification Service Examples

**1. Send Notification (Simulated)**
```bash
curl -X POST http://localhost:8090/api/v1/notify/send \
  -H "Content-Type: application/json" \
  -d '{
    "to": "customer@example.com",
    "subject": "Order Confirmation",
    "body": "Your order has been confirmed. Order ID: 12345"
  }' | jq .
```

---

## Check Service Health

**Eureka Dashboard:**
```bash
open http://localhost:8761/
# or curl to see registered services
curl http://localhost:8761/eureka/apps
```

**Check Specific Service Registration:**
```bash
curl http://localhost:8761/eureka/apps/INVENTORY-SERVICE | jq .
curl http://localhost:8761/eureka/apps/ORDER-SERVICE | jq .
curl http://localhost:8761/eureka/apps/PAYMENT-SERVICE | jq .
```

---

## OpenAPI JSON Endpoints (for Postman/Insomnia import)

```bash
# Get full OpenAPI spec
curl http://localhost:8080/v3/api-docs > inventory-api.json
curl http://localhost:8082/v3/api-docs > order-api.json
curl http://localhost:8081/v3/api-docs > payment-api.json
curl http://localhost:8090/v3/api-docs > notification-api.json

# View in formatted JSON
curl http://localhost:8080/v3/api-docs | jq .
```

---

## Postman Import Steps

1. Open **Postman**
2. Click **Import** (top-left)
3. Click **Link** tab
4. Paste: `http://localhost:8080/v3/api-docs`
5. Click **Continue** → **Import**
6. Collections auto-created with all endpoints
7. Use pre-built requests to test

---

## Testing Workflow Example

### Full End-to-End Flow

**1. Create an inventory item:**
```bash
INVENTORY_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/inventory \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "PROD-FLOW-001",
    "productName": "Test Product",
    "availableQuantity": 500,
    "reorderLevel": 50,
    "warehouse": "WH-MAIN"
  }')

echo "Inventory Created:"
echo $INVENTORY_RESPONSE | jq .

# Extract inventory ID from response
INVENTORY_ID=$(echo $INVENTORY_RESPONSE | jq -r '.data.inventoryId')
echo "Inventory ID: $INVENTORY_ID"
```

**2. Add stock to inventory:**
```bash
curl -s -X POST "http://localhost:8080/api/v1/inventory/add/PROD-FLOW-001?quantity=100" | jq .
```

**3. Reserve stock:**
```bash
curl -s -X POST "http://localhost:8080/api/v1/inventory/reserve/PROD-FLOW-001?quantity=20" | jq .
```

**4. Create an order:**
```bash
ORDER_RESPONSE=$(curl -s -X POST http://localhost:8082/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-001",
    "totalAmount": 5000.00,
    "status": "PENDING",
    "shippingAddress": "100 Test St, City",
    "billingAddress": "100 Test St, City",
    "notes": "Test order"
  }')

echo "Order Created:"
echo $ORDER_RESPONSE | jq .

ORDER_ID=$(echo $ORDER_RESPONSE | jq -r '.orderId')
echo "Order ID: $ORDER_ID"
```

**5. Create a payment:**
```bash
PAYMENT_RESPONSE=$(curl -s -X POST http://localhost:8081/api/v1/payments \
  -H "Content-Type: application/json" \
  -d "{
    \"orderId\": \"$ORDER_ID\",
    \"customerId\": \"CUST-001\",
    \"amount\": 5000.00,
    \"paymentMethod\": \"CARD\",
    \"status\": \"PENDING\"
  }")

echo "Payment Created:"
echo $PAYMENT_RESPONSE | jq .

PAYMENT_ID=$(echo $PAYMENT_RESPONSE | jq -r '.paymentId')
echo "Payment ID: $PAYMENT_ID"
```

**6. Process payment:**
```bash
curl -s -X POST "http://localhost:8081/api/v1/payments/$PAYMENT_ID/process?paymentMethod=card" | jq .
```

**7. Send notification:**
```bash
curl -s -X POST http://localhost:8090/api/v1/notify/send \
  -H "Content-Type: application/json" \
  -d '{
    "to": "customer@example.com",
    "subject": "Order Confirmation",
    "body": "Your order has been confirmed and payment processed."
  }' | jq .
```

---

## Response Examples

### Success Response (Inventory Create - 201):
```json
{
  "message": "Inventory created",
  "status": "OK",
  "statusCode": 201,
  "data": {
    "inventoryId": "550e8400-e29b-41d4-a716-446655440000",
    "productId": "SKU-001",
    "productName": "Product 1",
    "availableQuantity": 100,
    "reservedQuantity": 0,
    "reorderLevel": 20,
    "createdAt": "2026-02-26T10:30:00",
    "updatedAt": null,
    "warehouse": "WH-1"
  },
  "error": null,
  "exception": "NA"
}
```

### Error Response (Insufficient Stock - 400):
```json
{
  "message": "Insufficient stock to reserve",
  "status": "ERROR",
  "statusCode": 400,
  "data": null,
  "error": "INSUFFICIENT_STOCK",
  "exception": "NA"
}
```

### Server Error Response (500):
```json
{
  "message": "Failed to create inventory",
  "status": "ERROR",
  "statusCode": 500,
  "data": null,
  "error": "INVENTORY_CREATE_EXCEPTION",
  "exception": "Database connection failed"
}
```

---

## Useful Tools

- **jq**: Format JSON responses (`brew install jq` or `apt-get install jq`)
- **Postman**: Full GUI for API testing (https://www.postman.com/downloads/)
- **Insomnia**: Alternative REST client (https://insomnia.rest/)
- **curl**: Built-in terminal command

---

## Troubleshooting

**503 Service Unavailable after starting:**
- Wait 10-15 seconds for services to fully register with Eureka
- Check Eureka dashboard: http://localhost:8761/

**No response from endpoint:**
- Verify service is running: `ps aux | grep java`
- Check port is correct in application.properties
- Review service logs for errors

**Build fails:**
- Ensure Java 17+: `java -version`
- Clean Maven: `mvn clean`
- Update dependencies: `mvn dependency:resolve`

**Database errors (inventory/order/payment):**
- Verify PostgreSQL is running (for inventory/order/payment)
- Check DB credentials in application.properties
- Verify databases exist

---

Done! You now have:
- ✅ Swagger UI on all 4 services
- ✅ OpenAPI JSON specs for import
- ✅ curl commands for testing
- ✅ Postman integration ready
- ✅ Full end-to-end workflow examples

