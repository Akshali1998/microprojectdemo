# 🚀 Run Microservices with PostgreSQL (192.168.1.110:5432)

## ✅ Configuration Complete

All services are now configured to connect to your PostgreSQL server at:
- **Host:** 192.168.1.110
- **Port:** 5432
- **Username:** postgres
- **Password:** ind@6117

### Services & Ports

| Service | Port | Database | Connection |
|---------|------|----------|-----------|
| **Eureka Server** | 8761 | N/A | http://localhost:8761 |
| **Inventory Service** | 8080 | inventory_service_db | http://localhost:8080/api/v1/inventory |
| **Payment Service** | 8081 | payment_service_db | http://localhost:8081/api/v1/payments |
| **Order Service** | 8082 | order_service_db | http://localhost:8082/api/v1/orders |
| **Notification Service** | 8090 | N/A | http://localhost:8090/api/v1/notify/send |

---

## Step 1: Verify PostgreSQL Databases Exist

Connect to your PostgreSQL server at 192.168.1.110 and create the required databases:

```bash
psql -h 192.168.1.110 -U postgres
```

Then run in psql:
```sql
CREATE DATABASE order_service_db;
CREATE DATABASE payment_service_db;
CREATE DATABASE inventory_service_db;
CREATE DATABASE notification_service_db;

-- Verify
\l
```

Or via command line:
```bash
psql -h 192.168.1.110 -U postgres -c "CREATE DATABASE order_service_db;"
psql -h 192.168.1.110 -U postgres -c "CREATE DATABASE payment_service_db;"
psql -h 192.168.1.110 -U postgres -c "CREATE DATABASE inventory_service_db;"
psql -h 192.168.1.110 -U postgres -c "CREATE DATABASE notification_service_db;"
```

**Password:** `ind@6117`

---

## Step 2: Run Services from Main Classes

### Option A: From IntelliJ IDE (Recommended - See Live Output)

1. **Open IntelliJ:** File → Open → `/home/developer/SOFTWARE/ecommerce-microservices`
2. **Run Eureka Server first** (Service Registry):
   - Navigate to: `eureka-server` → `src/main/java` → `com.ecommerce.eureka` → `EurekaServerApplication`
   - Right-click → **Run 'EurekaServerApplication.main()'**
   - Wait for: `Tomcat initialized with port 8761`

3. **Run Inventory Service:**
   - Navigate to: `inventory-service` → `src/main/java` → `com.ecommerce.inventory` → `InventoryServiceApplication`
   - Right-click → **Run 'InventoryServiceApplication.main()'**
   - Wait for: `Tomcat initialized with port 8080`

4. **Run Payment Service:**
   - Navigate to: `payment-service` → `src/main/java` → `com.ecommerce.payment` → `PaymentServiceApplication`
   - Right-click → **Run 'PaymentServiceApplication.main()'**
   - Wait for: `Tomcat initialized with port 8081`

5. **Run Order Service:**
   - Navigate to: `order-service` → `src/main/java` → `com.ecommerce.order` → `OrderServiceApplication`
   - Right-click → **Run 'OrderServiceApplication.main()'**
   - Wait for: `Tomcat initialized with port 8082`

6. **Run Notification Service:**
   - Navigate to: `notification-service` → `src/main/java` → `com.ecommerce.notification` → `NotificationServiceApplication`
   - Right-click → **Run 'NotificationServiceApplication.main()'**
   - Wait for: `Tomcat initialized with port 8090`

### Option B: From Terminal (5 Separate Terminals)

```bash
cd /home/developer/SOFTWARE/ecommerce-microservices

# Terminal 1 - Eureka Server (Registry)
java -jar eureka-server/target/eureka-server-1.0.0.jar

# Terminal 2 - Inventory Service
java -jar inventory-service/target/inventory-service-1.0.0.jar

# Terminal 3 - Payment Service
java -jar payment-service/target/payment-service-1.0.0.jar

# Terminal 4 - Order Service
java -jar order-service/target/order-service-1.0.0.jar

# Terminal 5 - Notification Service
java -jar notification-service/target/notification-service-1.0.0.jar
```

---

## Step 3: Verify All Services Are Running

### Check Eureka Dashboard
Open: **http://localhost:8761**

You should see "Instances currently registered with Eureka":
- INVENTORY-SERVICE (port 8080)
- PAYMENT-SERVICE (port 8081)
- ORDER-SERVICE (port 8082)
- NOTIFICATION-SERVICE (port 8090)

### Test Endpoints

```bash
# Inventory Service
curl http://localhost:8080/api/v1/inventory

# Payment Service
curl http://localhost:8081/api/v1/payments

# Order Service
curl http://localhost:8082/api/v1/orders

# Notification Service (Send Email)
curl -X POST http://localhost:8090/api/v1/notify/send \
  -H "Content-Type: application/json" \
  -d '{
    "to": "user@example.com",
    "subject": "Test Email",
    "body": "This is a test message"
  }'
```

---

## Troubleshooting

### "Connection refused" Error (Still Getting It?)

This means the application can't reach 192.168.1.110:5432. Check:

1. **Is PostgreSQL running on 192.168.1.110?**
   ```bash
   psql -h 192.168.1.110 -U postgres -c "SELECT version();"
   ```
   - If this fails, PostgreSQL is not reachable at that IP/port.

2. **Can you ping the machine?**
   ```bash
   ping 192.168.1.110
   ```

3. **Is the firewall allowing port 5432?**
   ```bash
   # On the PostgreSQL server (192.168.1.110)
   sudo ufw allow 5432
   ```

4. **Is PostgreSQL listening on all interfaces (not just localhost)?**
   - Edit `/etc/postgresql/*/main/postgresql.conf` and ensure:
     ```
     listen_addresses = '*'
     ```
   - Then restart PostgreSQL

### "FATAL: database does not exist"

The connection succeeded, but the database wasn't found. Create it:
```bash
psql -h 192.168.1.110 -U postgres -c "CREATE DATABASE order_service_db;"
psql -h 192.168.1.110 -U postgres -c "CREATE DATABASE payment_service_db;"
psql -h 192.168.1.110 -U postgres -c "CREATE DATABASE inventory_service_db;"
```

### Incorrect Password Error

Ensure you're using the correct password: **ind@6117**

If you see "password authentication failed", double-check the password in:
- `order-service/src/main/resources/application.properties`
- `payment-service/src/main/resources/application.properties`
- `inventory-service/src/main/resources/application.properties`

All should have:
```
spring.datasource.username=postgres
spring.datasource.password=ind@6117
```

---

## Updated Configuration Files

✅ **order-service/src/main/resources/application.properties**
```
spring.datasource.url=jdbc:postgresql://192.168.1.110:5432/order_service_db
spring.datasource.username=postgres
spring.datasource.password=ind@6117
```

✅ **payment-service/src/main/resources/application.properties**
```
spring.datasource.url=jdbc:postgresql://192.168.1.110:5432/payment_service_db
spring.datasource.username=postgres
spring.datasource.password=ind@6117
```

✅ **inventory-service/src/main/resources/application.properties**
```
spring.datasource.url=jdbc:postgresql://192.168.1.110:5432/inventory_service_db
spring.datasource.username=postgres
spring.datasource.password=ind@6117
```

---

## Summary

✅ All services configured to use PostgreSQL at 192.168.1.110:5432  
✅ Credentials: postgres / ind@6117  
✅ Services built and ready to run from main classes  
✅ Eureka server for service discovery  
✅ Auto-register on startup  

**Ready to launch! Start Eureka first, then the other services.** 🚀

