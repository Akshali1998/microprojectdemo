# PostgreSQL Setup Guide for E-Commerce Microservices

## Step 1: Ensure PostgreSQL is Running

Check if PostgreSQL is running on your system:

```bash
# Linux/Mac
psql --version
psql -U postgres -c "SELECT version();"

# Or check if service is running
sudo systemctl status postgresql
```

If not running, start it:
```bash
# Linux
sudo systemctl start postgresql

# Mac (using Homebrew)
brew services start postgresql
```

---

## Step 2: Create Databases

Open PostgreSQL and create the required databases:

```bash
# Connect to PostgreSQL as postgres user
psql -U postgres
```

Then run these SQL commands in the psql prompt:

```sql
-- Create databases
CREATE DATABASE order_service_db;
CREATE DATABASE payment_service_db;
CREATE DATABASE inventory_service_db;
CREATE DATABASE notification_service_db;

-- Verify databases were created
\l
```

---

## Step 3: Service Ports and Configurations

All services are now configured to use **PostgreSQL on localhost:5432** with default postgres credentials:

| Service | Port | Database | Username | Password |
|---------|------|----------|----------|----------|
| Eureka Server | 8761 | N/A | - | - |
| Inventory Service | 8080 | inventory_service_db | postgres | postgres |
| Payment Service | 8081 | payment_service_db | postgres | postgres |
| Order Service | 8082 | order_service_db | postgres | postgres |
| Notification Service | 8090 | N/A | - | - |

---

## Step 4: Build All Services

```bash
cd /home/developer/SOFTWARE/ecommerce-microservices
mvn clean package -DskipTests
```

---

## Step 5: Run Services from Main Classes

### Option A: Run from IntelliJ IDE (Recommended)

1. **Open IntelliJ**
   - File → Open → `/home/developer/SOFTWARE/ecommerce-microservices`

2. **Run Eureka Server first** (Service Registry)
   - eureka-server → src/main/java → com.ecommerce.eureka → EurekaServerApplication
   - Right-click → Run 'EurekaServerApplication.main()'

3. **Run Inventory Service** (requires PostgreSQL inventory_service_db)
   - inventory-service → src/main/java → com.ecommerce.inventory → InventoryServiceApplication
   - Right-click → Run 'InventoryServiceApplication.main()'

4. **Run Payment Service** (requires PostgreSQL payment_service_db)
   - payment-service → src/main/java → com.ecommerce.payment → PaymentServiceApplication
   - Right-click → Run 'PaymentServiceApplication.main()'

5. **Run Order Service** (requires PostgreSQL order_service_db)
   - order-service → src/main/java → com.ecommerce.order → OrderServiceApplication
   - Right-click → Run 'OrderServiceApplication.main()'

6. **Run Notification Service** (Mail/Email endpoint)
   - notification-service → src/main/java → com.ecommerce.notification → NotificationServiceApplication
   - Right-click → Run 'NotificationServiceApplication.main()'

### Option B: Run from Terminal

```bash
cd /home/developer/SOFTWARE/ecommerce-microservices

# Terminal 1: Eureka Server
java -jar eureka-server/target/eureka-server-1.0.0.jar

# Terminal 2: Inventory Service
java -jar inventory-service/target/inventory-service-1.0.0.jar

# Terminal 3: Payment Service
java -jar payment-service/target/payment-service-1.0.0.jar

# Terminal 4: Order Service
java -jar order-service/target/order-service-1.0.0.jar

# Terminal 5: Notification Service
java -jar notification-service/target/notification-service-1.0.0.jar
```

---

## Step 6: Verify Services are Running

1. **Eureka Dashboard:** http://localhost:8761
   - Should show all 4 registered services:
     - INVENTORY-SERVICE
     - PAYMENT-SERVICE
     - ORDER-SERVICE
     - NOTIFICATION-SERVICE

2. **Test Endpoints:**

```bash
# Inventory Service
curl http://localhost:8080/api/v1/inventory

# Payment Service
curl http://localhost:8081/api/v1/payments

# Order Service
curl http://localhost:8082/api/v1/orders

# Notification Service (Send email)
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

### "FATAL: database does not exist"
**Solution:** Make sure you created the databases in PostgreSQL:
```bash
psql -U postgres
# Then run the SQL commands from Step 2
```

### "connection refused" / "Cannot connect to PostgreSQL"
**Solution:** Ensure PostgreSQL is running:
```bash
# Check status
sudo systemctl status postgresql

# If not running, start it
sudo systemctl start postgresql
```

### "Permission denied" for postgres user
**Solution:** Reset postgres password (if needed):
```bash
sudo -u postgres psql
ALTER USER postgres PASSWORD 'postgres';
\q
```

### Service won't start / "Unsupported class version"
**Solution:** Ensure you're using Java 17:
```bash
java -version  # Should show Java 17
```

### Port already in use
**Solution:** Kill the process using the port:
```bash
# Linux/Mac
lsof -ti:8080 | xargs kill -9  # Inventory (8080)
lsof -ti:8081 | xargs kill -9  # Payment (8081)
lsof -ti:8082 | xargs kill -9  # Order (8082)
lsof -ti:8090 | xargs kill -9  # Notification (8090)
lsof -ti:8761 | xargs kill -9  # Eureka (8761)
```

---

## PostgreSQL Connection Details

For any PostgreSQL tools or applications:
- **Host:** localhost
- **Port:** 5432
- **User:** postgres
- **Password:** postgres (default)
- **Databases:** order_service_db, payment_service_db, inventory_service_db

---

## Summary

✅ All services now use PostgreSQL  
✅ Services run from main classes via JAR files or IDE  
✅ Auto-register with Eureka on startup  
✅ Database schema created automatically via Hibernate (ddl-auto=update)  

**Ready to go! Start with Eureka, then run the other services.**

