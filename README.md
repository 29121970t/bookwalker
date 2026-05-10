# Bookwalker

Bookwalker is a full-stack bookstore application with a Spring Boot backend and a
Vue frontend. It supports catalog browsing, authentication, customer orders, and
admin workflows for managing books, authors, publishers, genres, tags, clients,
and orders.

## Stack

- Java 21
- Spring Boot 4
- Spring Web MVC, Spring Data JPA, Spring Security
- PostgreSQL
- JWT authentication
- Maven
- Checkstyle and JaCoCo
- Vue 3, Vite, TypeScript, Tailwind CSS
- shadcn-vue style component library
- Docker Compose
- GitHub Actions, SonarCloud, Render

## Project Structure

```text
.
├── src/main/java/com/kruosant/bookwalker   # Backend application
├── src/test/java/com/kruosant/bookwalker   # Backend tests
├── frontend                                # Vue frontend
├── compose.yaml                            # Local full-stack Docker setup
├── Dockerfile                              # Backend image
├── checkstyle.xml                          # Java style rules
└── .github/workflows                       # CI/CD and Sonar workflows
```

## Prerequisites

- JDK 21
- Maven, or the included Maven wrapper
- Node.js 24+ for the frontend
- PostgreSQL, unless you run the Docker Compose stack
- Docker and Docker Compose for containerized local runs

## Environment Variables

The backend reads its configuration from environment variables:

```env
DB_HOSTNAME=localhost
DB_PORT=5432
DB_NAME=bookwalker
DB_USER=bookwalker
DB_PASSWORD=bookwalker
PORT=8080
CORS_ALLOWED=http://localhost:5173,http://localhost:80
JWT_SECRET=replace-with-a-long-secret

ADMIN_SEED_ENABLED=false
ADMIN_EMAIL=admin@example.com
ADMIN_PASSWORD=admin-password
ADMIN_NAME=Admin
ADMIN_CITY=Minsk
```

For frontend development, point Vite at the local backend:

```env
VITE_API_URL=http://localhost:8080
```

## Run Locally

Start PostgreSQL first, then run the backend:

```bash
./mvnw spring-boot:run
```

The backend starts on the configured `PORT`, usually:

```text
http://localhost:8080
```

Run the frontend in a second terminal:

```bash
cd frontend
npm install
npm run dev
```

The Vite dev server starts on:

```text
http://localhost:5173
```

## Run With Docker Compose

Create a `.env` file with the backend variables, then run:

```bash
docker compose up --build
```

The compose stack starts:

- Frontend: `http://localhost`
- Backend: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

## Backend Commands

```bash
./mvnw checkstyle:check
./mvnw test
./mvnw clean package
./mvnw clean package jacoco:report
```

JaCoCo reports are written to:

```text
target/site/jacoco/index.html
```

## Frontend Commands

```bash
cd frontend
npm install
npm run dev
npm run build
npm run preview
```

## API Overview

Authentication:

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/auth/register` | Register a customer account |
| `POST` | `/auth/login` | Login and receive a JWT |
| `GET` | `/auth/me` | Get the authenticated user |

Catalog:

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/books` | List books |
| `GET` | `/books/{id}` | Get one book |
| `POST` | `/books` | Create a book |
| `PATCH` | `/books/{id}` | Partially update a book |
| `PUT` | `/books/{id}` | Replace a book |
| `POST` | `/books/{id}/cover` | Upload a book cover |
| `DELETE` | `/books/{id}` | Delete a book |
| `GET` | `/authors` | List authors |
| `GET` | `/authors/{id}` | Get one author |
| `POST` | `/authors` | Create an author |
| `PATCH` | `/authors/{id}` | Partially update an author |
| `PUT` | `/authors/{id}` | Replace an author |
| `DELETE` | `/authors/{id}` | Delete an author |
| `GET` | `/publishers` | List publishers |
| `GET` | `/publishers/{id}` | Get one publisher |
| `POST` | `/publishers` | Create a publisher |
| `PATCH` | `/publishers/{id}` | Partially update a publisher |
| `PUT` | `/publishers/{id}` | Replace a publisher |
| `DELETE` | `/publishers/{id}` | Delete a publisher |
| `GET` | `/genres` | List genres |
| `POST` | `/genres` | Create a genre |
| `PUT` | `/genres/{id}` | Replace a genre |
| `DELETE` | `/genres/{id}` | Delete a genre |
| `GET` | `/tags` | List tags |
| `POST` | `/tags` | Create a tag |
| `PUT` | `/tags/{id}` | Replace a tag |
| `DELETE` | `/tags/{id}` | Delete a tag |

Clients and orders:

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/clients` | List clients |
| `GET` | `/clients/{id}` | Get one client |
| `POST` | `/clients` | Create a client |
| `PATCH` | `/clients/{id}` | Partially update a client |
| `PUT` | `/clients/{id}` | Replace a client |
| `DELETE` | `/clients/{id}` | Delete a client |
| `GET` | `/orders` | List all orders |
| `GET` | `/orders/me` | List orders for the authenticated customer |
| `GET` | `/orders/{id}` | Get one order |
| `POST` | `/orders` | Create an order |
| `POST` | `/orders/bulk` | Create orders asynchronously in bulk |
| `PATCH` | `/orders/{id}` | Partially update an order |
| `PUT` | `/orders/{id}` | Replace an order |
| `DELETE` | `/orders/{id}` | Delete an order |

Utility:

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/counter/raceDemo` | Demonstrates safe and unsafe counter increments |
| `GET` | `/uploads/**` | Serves uploaded book cover files |

OpenAPI UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

## Authorization

- Public: authentication endpoints, uploaded files, Swagger/OpenAPI pages, and
  `GET` requests for books, authors, publishers, genres, and tags.
- Authenticated customer: `POST /orders` and `GET /orders/me`.
- Admin: client management, order administration, and catalog mutations.

Send protected requests with:

```http
Authorization: Bearer <token>
```

## CI/CD

The main CI/CD workflow runs on pushes to `main`:

- Builds the frontend with `npm run build`
- Runs backend Checkstyle
- Builds the backend and generates JaCoCo reports
- Runs backend tests
- Sends backend analysis to SonarCloud
- Deploys frontend and backend services to Render
- Runs Render health checks after deployment

## Notes

- Uploaded covers are stored in `./uploads` by default.
- PostgreSQL schema updates are handled by Hibernate with `ddl-auto=update`.
- The frontend component library lives in `frontend/src/components/ui`; unused
  generated components should be removed when they are no longer imported.
