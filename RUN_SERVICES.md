# How to Run Services from Main Classes

## Quick Summary
This project now has 5 microservices. Three are fully functional and can be run from their main classes:

| Service | Main Class | Port | Profile | Notes |
|---------|-----------|------|---------|-------|
| **Eureka Server** | `com.ecommerce.eureka.EurekaServerApplication` | 8761 | default | Service registry; start this FIRST |
| **Notification Service** (Mail) | `com.ecommerce.notification.NotificationServiceApplication` | 8090 | default | Simulates sending emails; registers with Eureka |
| **Order Service** | `com.ecommerce.order.OrderServiceApplication` | 8082 | local | Uses in-memory H2 DB; registers with Eureka |
| Inventory Service | `com.ecommerce.inventory.InventoryServiceApplication` | 8080 | default | Requires PostgreSQL (not in local profile) |
| Payment Service | `com.ecommerce.payment.PaymentServiceApplication` | 8081 | default | Requires PostgreSQL (not in local profile) |

---

## How to Run from IntelliJ IDE (Recommended - Shows Live Output)

### Step 1: Open the Project
1. **File** → **Open...**
2. Navigate to `/home/developer/SOFTWARE/ecommerce-microservices` and click **Open**
3. Click **Trust Project** if prompted
4. Wait for Maven to import (you'll see "Indexing..." progress)

### Step 2: Run Eureka Server First (Service Registry)
1. In the **Project** tool window, navigate to:  
   `eureka-server` → `src/main/java` → `com.ecommerce.eureka` → `EurekaServerApplication`
2. Right-click on `EurekaServerApplication` class
3. Click **Run 'EurekaServerApplication.main()'**
4. You should see Spring Boot startup logs in the **Run** tool window
5. Look for: `Tomcat initialized with port 8761 (http)`
6. Eureka UI should be available at: **http://localhost:8761**

### Step 3: Run Notification Service (Mail Endpoint)
1. In the **Project** tool window, navigate to:  
   `notification-service` → `src/main/java` → `com.ecommerce.notification` → `NotificationServiceApplication`
2. Right-click on `NotificationServiceApplication` class
3. Click **Run 'NotificationServiceApplication.main()'**
4. You should see: `Tomcat initialized with port 8090 (http)`
5. This service will register itself with Eureka
6. Test mail endpoint: **POST http://localhost:8090/api/v1/notify/send**  
   Body (JSON):
   ```json
   {
     "to": "user@example.com",
     "subject": "Test Email",
     "body": "This is a test"
   }
   ```

### Step 4: Run Order Service (Uses H2 In-Memory DB)
1. In the **Project** tool window, navigate to:  
   `order-service` → `src/main/java` → `com.ecommerce.order` → `OrderServiceApplication`
2. Right-click on `OrderServiceApplication` class
3. Click **Run 'OrderServiceApplication.main()'**
4. You should see: `Tomcat initialized with port 8082 (http)`
5. Test order endpoint: **GET http://localhost:8082/api/v1/orders**

### Step 5: View All Services in Eureka
1. Open browser: **http://localhost:8761**
2. Scroll down to see "Instances currently registered with Eureka"
3. You should see:
   - `NOTIFICATION-SERVICE` (port 8090)
   - `ORDER-SERVICE` (port 8082)

---

## How to Run from Command Line / Terminal

### Build All Services (from repo root)
```bash
cd /home/developer/SOFTWARE/ecommerce-microservices
mvn clean package -DskipTests
```

### Run Services (in separate terminals)

**Terminal 1 - Eureka Server:**
```bash
java -jar eureka-server/target/eureka-server-1.0.0.jar \
  --server.port=8761 \
  --eureka.client.register-with-eureka=false \
  --eureka.client.fetch-registry=false
```

**Terminal 2 - Notification Service:**
```bash
java -jar notification-service/target/notification-service-1.0.0.jar \
  --server.port=8090 \
  --eureka.client.service-url.defaultZone=http://localhost:8761/eureka
```

**Terminal 3 - Order Service (with local H2 profile):**
```bash
java -jar order-service/target/order-service-1.0.0.jar \
  --server.port=8082 \
  --spring.profiles.active=local \
  --eureka.client.service-url.defaultZone=http://localhost:8761/eureka
```

---

## Troubleshooting

### "Port X already in use"
**Solution:** Kill the process using that port:
```bash
# Linux/Mac:
lsof -ti:8761 | xargs kill -9  # For Eureka (port 8761)
lsof -ti:8090 | xargs kill -9  # For Notification (port 8090)
lsof -ti:8082 | xargs kill -9  # For Order (port 8082)
```

### "Cannot find main class"
**Solution:** In IntelliJ:
1. **File** → **Project Structure** → **Project**
2. Ensure **Project SDK** is set to Java 17
3. Click **Apply** and **OK**
4. **Build** → **Rebuild Project**

### Order Service fails to start with H2 error
**Solution:** H2 dependency is missing. The `order-service/pom.xml` should already have it, but if not:
1. Open `order-service/pom.xml`
2. Find the `<dependencies>` section
3. Add:
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```
4. **Maven** → **Reload Projects**

### "application-local.properties not found"
**Solution:** The file should be at `order-service/src/main/resources/application-local.properties`. If missing, it was already created in this setup. Just run Maven package again.

---

## What Each Service Does

### Eureka Server (Port 8761)
- Service discovery/registry server
- All services automatically register here
- UI at http://localhost:8761 shows registered instances

### Notification Service (Port 8090)
- REST endpoint for sending emails/notifications
- **Endpoint:** `POST /api/v1/notify/send`
- For local dev, it just logs the email instead of sending
- Registers with Eureka as `NOTIFICATION-SERVICE`

### Order Service (Port 8082)
- REST API for managing orders
- Uses in-memory H2 database (no external DB needed)
- **Endpoints:**
  - `GET /api/v1/orders` - List all orders
  - `GET /api/v1/orders/{orderId}` - Get specific order
  - `POST /api/v1/orders` - Create order
- Registers with Eureka as `ORDER-SERVICE`

---

## Pre-configured Run Configurations (IntelliJ)

If you see these in the Run dropdown, just select and click the green Run button:
- `eureka-server`
- `notification-service`
- `order-service`
- `All Services` (Compound - runs all three)

---

## Files Created/Modified

| File | Purpose |
|------|---------|
| `eureka-server/` | New module for service discovery |
| `notification-service/` | New module for mail/notifications |
| `order-service/src/main/resources/application-local.properties` | Local dev config (H2 DB) |
| `.idea/runConfigurations/eureka-server.xml` | IDE run config |
| `.idea/runConfigurations/notification-service.xml` | IDE run config |
| `.idea/runConfigurations/order-service.xml` | IDE run config |
| `run-services.sh` | Bash script to start all services (optional) |

---

## Next Steps

1. **Open the project in IntelliJ** (File → Open)
2. **Start Eureka Server** (Right-click main class → Run)
3. **Start Notification Service** (Right-click main class → Run)
4. **Start Order Service** (Right-click main class → Run)
5. **Visit http://localhost:8761** to see all services registered
6. **Test endpoints** using curl or Postman

That's it! Services are now running from their main classes.

