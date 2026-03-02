e# Database & Table Status Check

## How Tables are Created

When you **run the services**, Hibernate automatically creates the tables based on your entity classes.

Here's what happens:

1. **Service starts** → Connects to PostgreSQL at 192.168.1.110:5432
2. **Hibernate initializes** → Reads the `spring.jpa.hibernate.ddl-auto=update` setting
3. **Tables created** → Hibernate auto-creates tables from your @Entity classes in each database
4. **Service ready** → Application starts listening on its port

---

## Entity Classes (Define What Tables Look Like)

### Order Service - Creates `order` table
**File:** `order-service/src/main/java/com/ecommerce/order/model/Order.java`
```java
@Entity
@Table(name = "order")
public class Order {
    @Id
    private UUID orderId;
    private UUID customerId;
    private BigDecimal totalAmount;
    private String status;
    // ... more fields
}
```

### Inventory Service - Creates `inventory` table
**File:** `inventory-service/src/main/java/com/ecommerce/inventory/model/Inventory.java`
```java
@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productId;
    private int quantity;
    // ... more fields
}
```

### Payment Service - Creates `payment` table
**File:** `payment-service/src/main/java/com/ecommerce/payment/model/Payment.java`
```java
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID orderId;
    private BigDecimal amount;
    // ... more fields
}
```

---

## Current Status

### ✅ Databases Created
- ✓ order_service_db
- ✓ payment_service_db
- ✓ inventory_service_db
- ✓ notification_service_db

### ⏳ Tables Status
**Tables will be created WHEN you run the services**

| Database | Tables Created When | Service Port |
|----------|-------------------|--------------|
| order_service_db | OrderServiceApplication runs | 8082 |
| payment_service_db | PaymentServiceApplication runs | 8081 |
| inventory_service_db | InventoryServiceApplication runs | 8080 |
| notification_service_db | N/A (no DB tables) | 8090 |

---

## To See if Tables Were Created

After running services, check with:

```bash
# Check order_service_db tables
psql -h 192.168.1.110 -U postgres -d order_service_db -c "\dt"

# Check payment_service_db tables
psql -h 192.168.1.110 -U postgres -d payment_service_db -c "\dt"

# Check inventory_service_db tables
psql -h 192.168.1.110 -U postgres -d inventory_service_db -c "\dt"
```

### Expected Output
```
             List of relations
 Schema |   Name   | Type  |  Owner   
--------+----------+-------+----------
 public | inventory| table | postgres
 public | order    | table | postgres
 public | payment  | table | postgres
```

---

## Configuration That Creates Tables Automatically

In each service's `application.properties`:

```properties
# This tells Hibernate to AUTO-CREATE tables on startup
spring.jpa.hibernate.ddl-auto=update
```

**Options:**
- `create-drop` - Create tables, drop on shutdown (dev only)
- `create` - Create tables each time (destructive)
- `update` - Create/update tables if missing (✅ RECOMMENDED)
- `validate` - Just validate, don't create
- `none` - Do nothing

We use `update` so tables are created if missing, but existing data is preserved.

---

## Quick Visual Summary

```
┌─────────────────────────────────────────────────────────┐
│  PostgreSQL Server (192.168.1.110:5432)                 │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  ✓ order_service_db                                      │
│    └─ order (table - CREATED when OrderService runs)    │
│                                                           │
│  ✓ payment_service_db                                    │
│    └─ payment (table - CREATED when PaymentService runs)│
│                                                           │
│  ✓ inventory_service_db                                  │
│    └─ inventory (table - CREATED when InventoryService) │
│                                                           │
│  ✓ notification_service_db                               │
│    └─ (no tables - notification service uses REST only) │
│                                                           │
└─────────────────────────────────────────────────────────┘
```

---

## Next Step: Run Services to Create Tables

When you run the services:

```bash
cd /home/developer/SOFTWARE/ecommerce-microservices

# Terminal 1
java -jar eureka-server/target/eureka-server-1.0.0.jar

# Terminal 2
java -jar inventory-service/target/inventory-service-1.0.0.jar
# ← This creates `inventory` table in inventory_service_db

# Terminal 3
java -jar payment-service/target/payment-service-1.0.0.jar
# ← This creates `payment` table in payment_service_db

# Terminal 4
java -jar order-service/target/order-service-1.0.0.jar
# ← This creates `order` table in order_service_db

# Terminal 5
java -jar notification-service/target/notification-service-1.0.0.jar
```

**Look for these log messages:**
```
Tomcat initialized with port 8082 (http)  ← Order Service ready
Tomcat initialized with port 8081 (http)  ← Payment Service ready
Tomcat initialized with port 8080 (http)  ← Inventory Service ready
Tomcat initialized with port 8090 (http)  ← Notification Service ready
Tomcat initialized with port 8761 (http)  ← Eureka Service ready
```

---

## Summary

📊 **Current Status:**
- Databases: ✅ **CREATED** (4 databases)
- Tables: ⏳ **PENDING** (will be created when services run)

🚀 **What to Do:**
1. Run the 5 services (in separate terminals)
2. Wait for "Tomcat initialized" messages
3. Check tables with psql commands above

✅ **Done!**

b tabl