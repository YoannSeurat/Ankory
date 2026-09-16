# Ankory

Ankory is a small restaurant-ordering demo inspired by food delivery apps: a customer can browse a restaurant menu, add items to a cart, place an order, and track the order through a progression of statuses. A staff view lets the restaurant update order status from preparation to delivery to completion.

This project is built as a full-stack mini application:

- Spring Boot backend for the REST API and business logic
- H2 in-memory database for persistence during development
- Static web frontend for the customer and staff flows
- Seed data to populate restaurants and menu items automatically

---

## What this project does

The application models a lightweight food-order workflow:

1. A restaurant menu is loaded from the backend.
2. A customer chooses a restaurant and adds dishes to a cart.
3. The customer submits an order.
4. The backend creates a new order with a starting status.
5. Restaurant staff can advance the order through its lifecycle.
6. The client tracks the order in real time and displays the current step.

The status progression is:

- IN_PREPARATION
- EN_LIVRAISON
- LIVRE

This mimics the flow of a typical delivery or takeaway service.

---

## Tech stack

- Java 26 toolchain (configured in Gradle)
- Spring Boot 3.5
- Spring Web
- Spring Data JPA
- H2 in-memory database
- Plain HTML/CSS/JavaScript frontend

---

## Features

### Customer side

- Browse available restaurants
- View a restaurant menu with categories and prices
- Add items to a cart
- Update cart quantities
- Submit an order
- Track status progression from preparation to delivery to completion

### Staff side

- Switch to the staff view
- Select a restaurant
- View all orders for that restaurant
- Advance each order to the next status
- See the order line items and total amount

### Backend behavior

- Validate restaurant and menu item existence
- Reject invalid status transitions
- Expose structured DTO responses
- Seed starter menu data automatically when the database is empty

---

## Getting started

### Prerequisites

- JDK 26 or a compatible Java version matching the toolchain in Gradle
- Git
- A browser for the frontend

### 1) Clone the repository

```bash
git clone https://github.com/YoannSeurat/Ankory.git
cd Ankory
```

### 2) Run the backend

```bash
cd backend-service
./gradlew bootRun
```

On Windows, use:

```powershell
cd backend-service
gradlew.bat bootRun
```

The app will start on:

- http://localhost:8080

The frontend is served from the static folder, so the browser can open the main app directly on the backend origin.

### 3) Open the app

Visit:

```text
http://localhost:8080/
```

You should see the restaurant menu and ordering interface.

---

## Data and startup behavior

When the application launches, the backend checks if any restaurants exist in the database. If not, it loads seed data using `DataLoader`.

The seed data source is:

- `backend-service/src/main/resources/config/data.csv`

 If the file is missing or cannot be loaded, the app falls back to a default set of restaurant/menu entries; malformed rows are skipped.

This makes the application runnable immediately without external setup.

---

## Project structure

```text
Ankory/
├── backend-service/                  # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/ankory/
│   │   │   │   ├── config/           # seed/data loading
│   │   │   │   ├── controllers/      # REST API endpoints
│   │   │   │   ├── dto/              # request/response payloads
│   │   │   │   ├── entities/         # JPA models
│   │   │   │   ├── exceptions/       # custom exceptions and error handling
│   │   │   │   ├── repositories/     # Spring Data repositories
│   │   │   │   └── services/         # business rules
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── config/data.csv   # default restaurant/menu seed file
│   │   └── test/java/com/example/ankory/
│   ├── build.gradle
│   ├── gradlew / gradlew.bat
│   └── settings.gradle
├── frontend-client/                  # static HTML/CSS/JS frontend
│   ├── index.html
│   ├── app.js
│   ├── style.css
│   └── fonts/
├── README.md
└── .gitignore
```

---

## API overview

### Base URL

```text
http://localhost:8080
```

### Restaurants

#### List restaurants

```http
GET /restaurants
```

Example response:

```json
[
  {
    "id": 4,
    "name": "Sakura Sushi"
  }
]
```

#### Get restaurant menu

```http
GET /restaurants/{id}/menu
```

Example response:

```json
[
  {
    "id": 1,
    "name": "Cheeseburger",
    "category": "Plats",
    "description": "Steak de bœuf, cheddar, ketchup, moutarde",
    "price": 5
  }
]
```

### Orders

#### Create order

```http
POST /orders
```

Request body:

```json
{
  "restaurantId": 1,
  "lines": [
    { "menuItemId": 1, "quantity": 2 },
    { "menuItemId": 3, "quantity": 1 }
  ]
}
```

Example response:

```json
{
  "id": 1,
  "restaurantId": 1,
  "status": "IN_PREPARATION",
  "totalAmount": 25.00,
  "createdAt": "2026-09-16T12:00:00Z",
  "lines": [
    {
      "menuItemId": 1,
      "name": "Tomates Mozzarella",
      "quantity": 2,
      "unitPrice": 8.5
    }
  ]
}
```

#### List orders

```http
GET /orders
```

Optional filter:

```http
GET /orders?restaurantId=1
```

#### Get one order

```http
GET /orders/{id}
```

#### Update order status

```http
PATCH /orders/{id}/status
```

Request body:

```json
{
  "status": "EN_LIVRAISON"
}
```

Valid statuses:

- `IN_PREPARATION`
- `EN_LIVRAISON`
- `LIVRE`

The backend enforces valid transitions only:

- `IN_PREPARATION -> EN_LIVRAISON`
- `EN_LIVRAISON -> LIVRE`

---

## Frontend usage

The frontend is intentionally lightweight and meant to serve as a demo UI.

### Customer flow

1. Select a restaurant.
2. Browse the menu.
3. Add dishes to the cart.
4. Submit the order.
5. Follow the order status tracker on the page.

### Staff flow

1. Click the staff tab in the page.
2. Choose the restaurant.
3. View pending orders.
4. Click "Étape suivante" to advance each order.

---

## H2 database console

The app enables the H2 console for debugging and quick inspection.

Open:

```text
http://localhost:8080/h2-console
```

Use these connection details from the application properties:

- JDBC URL: `jdbc:h2:mem:ankorydb`
- Username: `ankoryadmin`
- Password: empty

---

## Running tests

```bash
cd backend-service
./gradlew test
```

This runs the project’s Spring Boot tests and checks the order status logic.

---

## Notes / project intent

This is a prototype project for learning and demonstrating:

- layered architecture in Spring Boot
- REST API design
- JPA entities and repositories
- domain validation and status transitions
- simple full-stack integration between backend and browser UI

It is intentionally focused on clarity and simplicity rather than production-level complexity.

---

## Summary

Ankory is a compact restaurant ordering and status-tracking app that demonstrates how to combine a Spring Boot API, a lightweight frontend, and in-memory data storage into a working end-to-end workflow. It is ideal as a learning project or a starting point for a larger food-ordering platform.

