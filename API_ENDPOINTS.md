# API Endpoints Reference — ecommerce-microservices

This file lists all REST endpoints in the repository, grouped by service, with short explanations, request shapes (DTO fields) and example "industrial-style" responses (status/message/data/exception). Use this as a quick reference when testing with Postman or curl.

---

## Conventions
- Base paths and ports used by default in the repo:
  - Inventory Service: http://localhost:8080 (base path `/api/v1/inventory`)
  - Payment Service: http://localhost:8081 (base path `/api/v1/payments`)
  - Order Service:   http://localhost:8082 (base path `/api/v1/orders`)
  - Notification Service: http://localhost:8090 (base path `/api/v1/notify`)
  - Eureka Server: http://localhost:8761/

- Responses from inventory endpoints use `ApiResponse<T>` with fields:
  - `message` (String), `status` ("OK" or "ERROR"), `statusCode` (int), `data` (payload), `error` (error code or null), `exception` (exception message or "NA").

---

## Inventory Service (inventory-service)
Base: `/api/v1/inventory`

DTO: `InventoryDTO`
- inventoryId: UUID
- productId: String
- productName: String
- availableQuantity: int
- reservedQuantity: int
- reorderLevel: int
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
- warehouse: String

Endpoints:

1) Create inventory
- Method: POST
- URL: `/api/v1/inventory`
- Request body (JSON):
  {
    "productId": "SKU-12345",
    "productName": "Acme Widget",
    "availableQuantity": 150,
    "reorderLevel": 10,
    "warehouse": "WH-1"
  }
- Successful response (201 Created):
  {
    "message": "Inventory created",
    "status": "OK",
    "statusCode": 201,
    "data": { /* InventoryDTO; inventoryId generated */ },
    "error": null,
    "exception": "NA"
  }

2) Get inventory by inventoryId
- Method: GET
- URL: `/api/v1/inventory/{inventoryId}`
- Response (200 OK): ApiResponse<InventoryDTO>

3) Get inventory by productId
- Method: GET
- URL: `/api/v1/inventory/product/{productId}`
- Response (200 OK): ApiResponse<InventoryDTO>

4) Get all inventory
- Method: GET
- URL: `/api/v1/inventory`
- Response (200 OK): ApiResponse<List<InventoryDTO>>

5) Get low stock items
- Method: GET
- URL: `/api/v1/inventory/low-stock`
- Response (200 OK): ApiResponse<List<InventoryDTO>>

6) Get inventory by warehouse
- Method: GET
- URL: `/api/v1/inventory/warehouse/{warehouse}`
- Response (200 OK): ApiResponse<List<InventoryDTO>>

7) Add stock
- Method: POST
- URL: `/api/v1/inventory/add/{productId}?quantity={n}`
- Example: `/api/v1/inventory/add/SKU-12345?quantity=20`
- Response (200 OK): ApiResponse<InventoryDTO> (updated quantities)

8) Reserve stock
- Method: POST
- URL: `/api/v1/inventory/reserve/{productId}?quantity={n}`
- Response (200 OK) on success or (400 Bad Request) with error `INSUFFICIENT_STOCK`.

9) Release stock
- Method: POST
- URL: `/api/v1/inventory/release/{productId}?quantity={n}`
- Response (200 OK): ApiResponse<InventoryDTO>

10) Low-stock count
- Method: GET
- URL: `/api/v1/inventory/stats/low-stock-count`
- Response (200 OK): ApiResponse<Long> (number of low-stock items)

---

## Order Service (order-service)
Base: `/api/v1/orders`

DTO: `OrderDTO`
- orderId: UUID
- customerId: UUID
- totalAmount: BigDecimal
- status: String
- shippingAddress: String
- billingAddress: String
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
- notes: String

Endpoints:

1) Create order
- Method: POST
- URL: `/api/v1/orders`
- Request body: OrderDTO (omit orderId; service sets it)
- Response (201 Created): OrderDTO (created resource)

2) Get order by id
- Method: GET
- URL: `/api/v1/orders/{orderId}`
- Response (200 OK) or 404 Not Found

3) Get all orders
- Method: GET
- URL: `/api/v1/orders`
- Response: List<OrderDTO>

4) Get orders by customer
- Method: GET
- URL: `/api/v1/orders/customer/{customerId}`

5) Get orders by status
- Method: GET
- URL: `/api/v1/orders/status/{status}`

6) Update order status
- Method: PATCH
- URL: `/api/v1/orders/{orderId}/status?status={newStatus}`
- Response: OrderDTO (updated)

7) Update order
- Method: PUT
- URL: `/api/v1/orders/{orderId}`
- Request body: OrderDTO
- Response: OrderDTO (updated)

8) Delete order
- Method: DELETE
- URL: `/api/v1/orders/{orderId}`
- Response: 204 No Content

9) Get total orders by status
- Method: GET
- URL: `/api/v1/orders/stats/status/{status}`
- Response: long (count)

Notes: Order service uses H2 in-memory by default in this repo (check `application.properties`).

---

## Payment Service (payment-service)
Base: `/api/v1/payments`

DTO: `PaymentDTO` (fields used by controllers/service)
- paymentId: UUID
- orderId: UUID
- customerId: UUID
- amount: BigDecimal
- paymentMethod: String
- status: String
- remarks: String
- createdAt, updatedAt

Endpoints:

1) Create payment
- Method: POST
- URL: `/api/v1/payments`
- Request body: PaymentDTO (omit paymentId; service sets it)
- Response (201 Created): PaymentDTO

2) Get payment by id
- Method: GET
- URL: `/api/v1/payments/{paymentId}`

3) Get payment by order id
- Method: GET
- URL: `/api/v1/payments/order/{orderId}`

4) Get all payments
- Method: GET
- URL: `/api/v1/payments`

5) Get payments by customer
- Method: GET
- URL: `/api/v1/payments/customer/{customerId}`

6) Get payments by status
- Method: GET
- URL: `/api/v1/payments/status/{status}`

7) Process payment
- Method: POST
- URL: `/api/v1/payments/{paymentId}/process?paymentMethod={method}`
- Response: PaymentDTO (processed)

8) Update payment status
- Method: PATCH
- URL: `/api/v1/payments/{paymentId}/status?status={newStatus}`

9) Delete payment
- Method: DELETE
- URL: `/api/v1/payments/{paymentId}`

10) Payments stats
- Method: GET
- URL: `/api/v1/payments/stats/status/{status}`
- Response: long (count)

---

## Notification Service (notification-service)
Base: `/api/v1/notify`

Endpoints:

1) Send mail (simulated)
- Method: POST
- URL: `/api/v1/notify/send`
- Request body (example):
  {
    "to": "user@example.com",
    "subject": "Your order has shipped",
    "body": "Order #..."
  }
- Response (200 OK): { "status": "sent-simulated" }

---

## Eureka Server (eureka-server)
- UI: `http://localhost:8761/` (shows registered instances)
- Programmatic registry: `http://localhost:8761/eureka/apps`

---

## Example industrial-style ApiResponse (inventory)
Successful create response (201):

{
  "message": "Inventory created",
  "status": "OK",
  "statusCode": 201,
  "data": {
    "inventoryId": "c41733d1-141d-440a-903c-0dfcf42595f7",
    "productId": "sku-12345",
    "productName": "Acme Widget",
    "availableQuantity": 150,
    "reservedQuantity": 0,
    "reorderLevel": 10,
    "createdAt": "2026-02-20T17:05:05.581675",
    "updatedAt": null,
    "warehouse": "WH-1"
  },
  "error": null,
  "exception": "NA"
}

Error example (insufficient stock reserve):
{
  "message": "Insufficient stock to reserve",
  "status": "ERROR",
  "statusCode": 400,
  "data": null,
  "error": "INSUFFICIENT_STOCK",
  "exception": "NA"
}

---

## Notes & tips
- Make sure you run the Eureka server before starting other services so they can register.
- Check each service `application.properties` for `server.port` and `eureka.client.service-url.defaultZone` if you change ports.
- For Inventory endpoints the `ApiResponse` wrapper is used; for Order and Payment controllers the DTOs are returned directly (standard HTTP codes).

---

## Swagger / OpenAPI (added)

Each service now exposes OpenAPI docs and a Swagger UI (via springdoc). Default URLs (change host/port if you customized):

- Inventory Service (port 8080):
  - Swagger UI: http://localhost:8080/swagger-ui/index.html
  - OpenAPI JSON: http://localhost:8080/v3/api-docs

- Order Service (port 8082):
  - Swagger UI: http://localhost:8082/swagger-ui/index.html
  - OpenAPI JSON: http://localhost:8082/v3/api-docs

- Payment Service (port 8081):
  - Swagger UI: http://localhost:8081/swagger-ui/index.html
  - OpenAPI JSON: http://localhost:8081/v3/api-docs

- Notification Service (port 8090):
  - Swagger UI: http://localhost:8090/swagger-ui/index.html
  - OpenAPI JSON: http://localhost:8090/v3/api-docs

Example full resource URLs with sample IDs you can paste into Swagger or Postman:

- Create inventory (POST): http://localhost:8080/api/v1/inventory
- Get inventory by id (GET): http://localhost:8080/api/v1/inventory/c41733d1-141d-440a-903c-0dfcf42595f7
- Add stock (POST): http://localhost:8080/api/v1/inventory/add/sku-12345?quantity=20
- Reserve stock (POST): http://localhost:8080/api/v1/inventory/reserve/sku-12345?quantity=5
- Create order (POST): http://localhost:8082/api/v1/orders
- Get order by id (GET): http://localhost:8082/api/v1/orders/11111111-2222-3333-4444-555555555555
- Create payment (POST): http://localhost:8081/api/v1/payments
- Process payment (POST): http://localhost:8081/api/v1/payments/aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee/process?paymentMethod=card
- Send notification (POST): http://localhost:8090/api/v1/notify/send

Note: After you start each service (Eureka first), you can open the service Swagger UI to explore and try endpoints interactively.

---
