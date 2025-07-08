# Orders Management System

A modern full-stack web application for managing client orders, built with a Spring Boot backend and a React (Vite) frontend. Features authentication, order and client management, and a clean, responsive UI inspired by professional SaaS dashboards.

---

## Features
- User authentication (login, session)
- Manage clients and orders
- Modern, responsive UI (Poppins font, MUI, custom theming)
- Filtering, searching, and pagination
- RESTful API (Spring Boot)
- MySQL (prod) or H2 (dev) database

---

## Tech Stack
- **Backend:** Java 17, Spring Boot, Spring Security, JPA, MySQL/H2
- **Frontend:** React 18, Vite, Material-UI (MUI), Axios

---

## Getting Started

### Prerequisites
- Node.js (v18+ recommended)
- Java 17+
- Maven

### Backend Setup
1. `cd orders-app`
2. Copy `env.properties.template` to `env.properties` and adjust DB settings if needed.
3. Run with H2 (dev):
   ```sh
   ./mvnw spring-boot:run
   ```
   Or with MySQL (prod):
   - Set your MySQL credentials in `env.properties` and `application.properties`.
   - Start your MySQL server.
   - Run the app as above.

### Frontend Setup
1. `cd orders-client`
2. Install dependencies:
   ```sh
   npm install
   ```
3. Start the dev server:
   ```sh
   npm run dev
   ```
4. Open [http://localhost:5173](http://localhost:5173) in your browser.

---

## Usage
- Log in with seeded admin credentials (see backend seeder or ask your admin).
- Manage orders and clients from the dashboard.
- Use filters, search, and pagination for large datasets.

---

## Screenshots
> _Add screenshots of the login page, orders dashboard, and client management here._

---

## License
MIT. See [LICENSE](LICENSE) for details. 