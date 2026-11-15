# Support Ticket API

Java 17 Spring Boot REST API for creating and managing support tickets. It uses MySQL, Spring Data JPA, Spring Security, and JWT bearer authentication.

## Prerequisites

- Java 17
- Maven 3.6+
- A running MySQL server

The default local settings connect to `root` / `root` on MySQL and create the `support_ticket` database if the MySQL account has permission. Override them before running outside local development:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/support_ticket?useSSL=false&serverTimezone=UTC"
$env:DB_USERNAME = "your_user"
$env:DB_PASSWORD = "your_password"
$env:JWT_SECRET = "a-unique-secret-with-at-least-thirty-two-characters"
mvn spring-boot:run
```

Health is available at `GET /actuator/health`.

## Modern Angular UI

The Angular 18 single-page dashboard is in [`frontend`](frontend). It provides registration/login, JWT storage and forwarding, ticket creation, support queue and filters, status editing, and Admin-only deletion controls.

Start the API first, then start the UI in a second terminal:

```powershell
cd frontend
npm install
npm start
```

Open `http://localhost:4200`. The API permits this development origin through its CORS configuration. The UI uses `http://localhost:8080/api`; change `API_URL` in `frontend/src/app/api.service.ts` if the backend has a different address.

## Authentication

Public registration always creates a `USER`. The app seeds these local-only privileged accounts on first run; change the passwords directly in `SeedDataConfig` before any shared deployment:

| Username | Role | Password |
| --- | --- | --- |
| `support` | `SUPPORT` | `ChangeMeSupport123!` |
| `admin` | `ADMIN` | `ChangeMeAdmin123!` |

Register or log in, then add `Authorization: Bearer <accessToken>` to protected requests.

```http
POST /api/auth/register
Content-Type: application/json

{"username":"alice","password":"long-enough-password"}
```

## Ticket endpoints

| Method | Path | Authorization |
| --- | --- | --- |
| POST | `/api/tickets` | any authenticated user |
| GET | `/api/tickets` | SUPPORT or ADMIN |
| GET | `/api/tickets/{id}` | SUPPORT or ADMIN |
| PUT | `/api/tickets/{id}` | SUPPORT or ADMIN |
| DELETE | `/api/tickets/{id}` | ADMIN only |

Example create payload:

```json
{"title":"Cannot sign in","description":"The login page rejects my password.","priority":"HIGH"}
```

New tickets begin as `OPEN`. Accepted status values are `OPEN`, `IN_PROGRESS`, and `RESOLVED`; priorities are `LOW`, `MEDIUM`, `HIGH`, and `CRITICAL`.

The API returns JSON error bodies for validation failures (400), duplicate usernames (409), missing tickets (404), unauthenticated requests (401), and unauthorized roles (403).
