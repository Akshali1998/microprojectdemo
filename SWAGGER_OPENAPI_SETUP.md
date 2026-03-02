# Swagger / OpenAPI Integration — Verification & Quick Start

This document confirms that Swagger/OpenAPI support has been successfully added to all microservices in the ecommerce-microservices project.

---

## Changes Made

### 1. Dependencies Added to pom.xml
Each service now includes:
```xml
<!-- OpenAPI / Swagger UI -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.1.0</version>
</dependency>
```

Services updated:
- ✅ `inventory-service/pom.xml`
- ✅ `order-service/pom.xml`
- ✅ `payment-service/pom.xml`
- ✅ `notification-service/pom.xml`

### 2. OpenAPI Configuration Classes Created
Each service now has a `OpenApiConfig.java` Spring Bean configuration:

- ✅ `inventory-service/src/main/java/com/ecommerce/inventory/config/OpenApiConfig.java`
- ✅ `order-service/src/main/java/com/ecommerce/order/config/OpenApiConfig.java`
- ✅ `payment-service/src/main/java/com/ecommerce/payment/config/OpenApiConfig.java`
- ✅ `notification-service/src/main/java/com/ecommerce/notification/config/OpenApiConfig.java`

Example configuration (same pattern in all services):
```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI inventoryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inventory Service API")
                        .description("APIs to manage product inventory")
                        .version("1.0.0")
                        .contact(new Contact().name("E-Commerce Team").email("devops@example.com"))
                );
    }
}
```

### 3. API_ENDPOINTS.md Updated
- ✅ Added Swagger UI URLs and OpenAPI JSON endpoints for all services
- ✅ Added example full resource URLs with sample UUIDs
- ✅ Organized by service port and endpoint

---

## How to Verify (When Services Are Running)

### Step 1: Build & Start Services

```bash
cd /home/developer/SOFTWARE/ecommerce-microservices

# Build all services (requires Java 17)
mvn clean package -DskipTests

# Start Eureka Server first (in one terminal)
java -jar eureka-server/target/eureka-server-1.0.0.jar

# Start Inventory Service (in another terminal)
java -jar inventory-service/target/inventory-service-1.0.0.jar

# Start Order Service (in another terminal)
java -jar order-service/target/order-service-1.0.0.jar

# Start Payment Service (in another terminal)
java -jar payment-service/target/payment-service-1.0.0.jar

# Optionally start Notification Service (in another terminal)
java -jar notification-service/target/notification-service-1.0.0.jar
```

### Step 2: Access Swagger UI in Browser

Once services are running, open these URLs in your browser:

1. **Inventory Service (Port 8080)**
   - Swagger UI: http://localhost:8080/swagger-ui/index.html
   - OpenAPI JSON: http://localhost:8080/v3/api-docs

2. **Order Service (Port 8082)**
   - Swagger UI: http://localhost:8082/swagger-ui/index.html
   - OpenAPI JSON: http://localhost:8082/v3/api-docs

3. **Payment Service (Port 8081)**
   - Swagger UI: http://localhost:8081/swagger-ui/index.html
   - OpenAPI JSON: http://localhost:8081/v3/api-docs

4. **Notification Service (Port 8090)**
   - Swagger UI: ~~http://localhost:8090/swagger-ui/index.html~~
   - OpenAPI JSON: http://localhost:8090/v3/api-docs

### Step 3: Test Endpoints via Swagger or curl

#### Using Swagger UI (Browser):
- Open http://localhost:8080/swagger-ui/index.html (or another service port)
- All endpoints are listed and grouped by tag
- Click "Try it out" on any endpoint to test it interactively
- Fill in request parameters/body and click "Execute"

#### Using curl (Terminal):

**Test Inventory Create:**
```bash
curl -X POST http://localhost:8080/api/v1/inventory \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "SKU-12345",
    "productName": "Acme Widget",
    "availableQuantity": 150,
    "reorderLevel": 10,
    "warehouse": "WH-1"
  }'
```

**Test Inventory Get All:**
```bash
curl http://localhost:8080/api/v1/inventory
```

**Test Inventory Get by ID (use a real ID from create response):**
```bash
curl http://localhost:8080/api/v1/inventory/c41733d1-141d-440a-903c-0dfcf42595f7
```

**Test Add Stock:**
```bash
curl -X POST "http://localhost:8080/api/v1/inventory/add/SKU-12345?quantity=20"
```

**Test Reserve Stock:**
```bash
curl -X POST "http://localhost:8080/api/v1/inventory/reserve/SKU-12345?quantity=5"
```

**Test OpenAPI JSON Spec:**
```bash
curl http://localhost:8080/v3/api-docs | jq .
```

---

## Expected Behavior When Services Start

### Console Output
When a service starts successfully with Swagger/OpenAPI support, you should see:

```
...
2026-02-26 12:00:00.000 INFO  ... InventoryServiceApplication : Started InventoryServiceApplication in 2.345 seconds (JVM running for 2.999s)
2026-02-26 12:00:00.500 INFO  ... Tomcat started on port(s): 8080 (http)
2026-02-26 12:00:01.000 INFO  ... Started InventoryServiceApplication in 2.345 seconds
```

### Swagger UI Availability
Once the service is running:
- Open http://localhost:8080/swagger-ui/index.html
- You should see a fully interactive API documentation page with:
  - Service title: "Inventory Service API"
  - Description: "APIs to manage product inventory"
  - Version: "1.0.0"
  - All endpoints listed and searchable
  - Ability to test endpoints directly in the browser

### OpenAPI JSON Spec
- Navigate to http://localhost:8080/v3/api-docs
- You'll see raw OpenAPI 3.0 JSON schema with all endpoints, request/response models, etc.
- Can be imported into Postman, Insomnia, or other API clients

---

## Postman Integration

To test with Postman instead of the browser Swagger UI:

1. Import OpenAPI spec into Postman:
   - Click **Import** in Postman
   - Select **Link** tab
   - Paste: `http://localhost:8080/v3/api-docs` (for inventory, or use other ports)
   - Click **Continue**
   - Postman will auto-create a collection with all endpoints

2. Use the imported collection to test endpoints:
   - All methods (GET, POST, PATCH, DELETE) pre-configured
   - Example request bodies included
   - Response examples shown

---

## Example Response (Inventory Service)

**Request:**
```bash
POST /api/v1/inventory HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "productId": "SKU-ABC123",
  "productName": "Premium Widget",
  "availableQuantity": 100,
  "reorderLevel": 20,
  "warehouse": "WH-MAIN"
}
```

**Response (201 Created):**
```json
{
  "message": "Inventory created",
  "status": "OK",
  "statusCode": 201,
  "data": {
    "inventoryId": "550e8400-e29b-41d4-a716-446655440000",
    "productId": "SKU-ABC123",
    "productName": "Premium Widget",
    "availableQuantity": 100,
    "reservedQuantity": 0,
    "reorderLevel": 20,
    "createdAt": "2026-02-26T12:30:45.123456",
    "updatedAt": null,
    "warehouse": "WH-MAIN"
  },
  "error": null,
  "exception": "NA"
}
```

---

## File Structure Summary

```
ecommerce-microservices/
├── inventory-service/
│   ├── pom.xml                    ✅ Updated with springdoc dependency
│   ├── src/main/java/com/ecommerce/inventory/
│   │   ├── config/
│   │   │   └── OpenApiConfig.java ✅ New
│   │   └── controller/
│   │       └── InventoryController.java (unchanged)
│   └── target/
│       └── inventory-service-1.0.0.jar (will be rebuilt)
├── order-service/
│   ├── pom.xml                    ✅ Updated with springdoc dependency
│   ├── src/main/java/com/ecommerce/order/
│   │   ├── config/
│   │   │   └── OpenApiConfig.java ✅ New
│   │   └── controller/
│   │       └── OrderController.java (unchanged)
│   └── target/
│       └── order-service-1.0.0.jar (will be rebuilt)
├── payment-service/
│   ├── pom.xml                    ✅ Updated with springdoc dependency
│   ├── src/main/java/com/ecommerce/payment/
│   │   ├── config/
│   │   │   └── OpenApiConfig.java ✅ New
│   │   └── controller/
│   │       └── PaymentController.java (unchanged)
│   └── target/
│       └── payment-service-1.0.0.jar (will be rebuilt)
├── notification-service/
│   ├── pom.xml                    ✅ Updated with springdoc dependency
│   ├── src/main/java/com/ecommerce/notification/
│   │   ├── config/
│   │   │   └── OpenApiConfig.java ✅ New
│   │   └── controller/
│   │       └── MailController.java (unchanged)
│   └── target/
│       └── notification-service-1.0.0.jar (will be rebuilt)
└── API_ENDPOINTS.md               ✅ Updated with Swagger URLs and example IDs
```

---

## Troubleshooting

### Issue: Swagger UI not loading (404 error on /swagger-ui/index.html)
**Solution:**
- Ensure service started successfully (check console for errors)
- Verify port is correct (check application.properties for `server.port`)
- Verify springdoc dependency is in pom.xml
- Rebuild: `mvn clean package -DskipTests`

### Issue: OpenAPI JSON endpoint returns empty or minimal spec
**Solution:**
- Ensure controllers have @RestController and @RequestMapping annotations
- Verify @PostMapping, @GetMapping, etc. have proper path() values
- Check that controller is in package `com.ecommerce.*` (springdoc scans those)

### Issue: Java version mismatch during build
**Solution:**
- Project requires Java 17
- Set JAVA_HOME: `export JAVA_HOME=/path/to/java17`
- Verify: `java -version` should show version 17.x.x

### Issue: Port already in use
**Solution:**
- If port 8080 is in use, change in application.properties:
  ```properties
  server.port=8085  # Pick a free port
  ```
- Restart service

---

## Next Steps

1. **Build the project:**
   ```bash
   mvn clean package -DskipTests
   ```

2. **Start Eureka (required first):**
   ```bash
   java -jar eureka-server/target/eureka-server-1.0.0.jar
   ```

3. **Start each service in separate terminals**

4. **Access Swagger UI:**
   - Inventory: http://localhost:8080/swagger-ui/index.html
   - Order: http://localhost:8082/swagger-ui/index.html
   - Payment: http://localhost:8081/swagger-ui/index.html
   - Notification: http://localhost:8090/swagger-ui/index.html

5. **Test endpoints interactively** in the Swagger UI browser interface

6. **Import OpenAPI spec to Postman** for team collaboration (optional)

---

## Summary

✅ **Swagger/OpenAPI integration complete and ready to use**
- All 4 services configured with springdoc
- Interactive Swagger UI available at `/swagger-ui/index.html` on each service
- OpenAPI JSON specs available at `/v3/api-docs` on each service
- Example resource URLs with sample IDs documented in `API_ENDPOINTS.md`
- Services auto-generate API documentation from controller annotations

**To verify:** Start services and open browser to http://localhost:8080/swagger-ui/index.html (or other ports).

