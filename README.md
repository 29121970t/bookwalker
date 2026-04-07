
# Bookwalker API

A simple RESTful API for managing a bookshop. Built with Spring Boot, this application provides endpoints to create, retrieve, update and delete books, authors, publishers, clients and orders.

## Endpoints

### Books

| Method | Endpoint | Description | Request |
|------|------|------|------|
| GET | `/books` | Retrieve all books | None |
| GET | `/books/{id}` | Retrieve a book by ID | None |
| GET | `/books/search?name=` | Search books by name (partial match) | None |
| POST | `/books` | Create a new book | `BookCreateDto` JSON |
| PATCH | `/books/{id}` | Partially update book | `BookPatchDto` JSON |
| PUT | `/books/{id}` | Fully update book | `BookPutDto` JSON |
| DELETE | `/books/{id}` | Delete book | None |
| POST | `/books/{bookId}/authors/{authorId}` | Add author to book | None |
| DELETE | `/books/{bookId}/authors/{authorId}` | Remove author from book | None |
| POST | `/books/{bookId}/publisher/{publisherId}` | Set book publisher | None |

---

### Authors

| Method | Endpoint | Description | Request |
|------|------|------|------|
| GET | `/authors` | Retrieve all authors | None |
| GET | `/authors/{id}` | Retrieve author by ID | None |
| POST | `/authors` | Create a new author | `AuthorCreateDto` JSON |
| PATCH | `/authors/{id}` | Partially update author | `AuthorPatchDto` JSON |
| PUT | `/authors/{id}` | Fully update author | `AuthorPutDto` JSON |
| DELETE | `/authors/{id}` | Delete author | None |
| POST | `/authors/{authorId}/books/{bookId}` | Add book to author | None |
| DELETE | `/authors/{authorId}/books/{bookId}` | Remove book from author | None |

---

### Publishers

| Method | Endpoint | Description | Request |
|------|------|------|------|
| GET | `/publishers` | Retrieve all publishers | None |
| GET | `/publishers/{id}` | Retrieve publisher by ID | None |
| POST | `/publishers` | Create publisher | `PublisherCreateDto` JSON |
| PATCH | `/publishers/{id}` | Partially update publisher | `PublisherPatchDto` JSON |
| PUT | `/publishers/{id}` | Fully update publisher | `PublisherPutDto` JSON |
| DELETE | `/publishers/{id}` | Delete publisher | None |
| POST | `/publishers/{publisherId}/books/{bookId}` | Add book to publisher | None |
| DELETE | `/publishers/{publisherId}/books/{bookId}` | Remove book from publisher | None |

---

### Clients

| Method | Endpoint | Description | Request |
|------|------|------|------|
| GET | `/clients` | Retrieve all clients | None |
| GET | `/clients/{id}` | Retrieve client by ID | None |
| POST | `/clients` | Create a client | `ClientCreateDto` JSON |
| PATCH | `/clients/{id}` | Partially update client | `ClientPatchDto` JSON |
| PUT | `/clients/{id}` | Fully update client | `ClientPutDto` JSON |
| DELETE | `/clients/{id}` | Delete client | None |

---

### Orders

| Method | Endpoint | Description | Request |
|------|------|------|------|
| GET | `/orders` | Retrieve all orders | None |
| GET | `/orders/{id}` | Retrieve order by ID | None |
| POST | `/orders` | Create order | `OrderCreateDto` JSON |
| POST | `/orders/bulk` | Bulk create orders atomically | `OrderCreateDto[]` JSON |
| POST | `/orders/bulk/no-transaction` | Bulk create orders without atomicity | `OrderCreateDto[]` JSON |
| PATCH | `/orders/{id}` | Partially update order | `OrderPatchDto` JSON |
| PUT | `/orders/{id}` | Fully update order | `OrderPutDto` JSON |
| DELETE | `/orders/{id}` | Delete order | None |

---

## Data Model

The application contains the following main entities:

- **Book** – represents a book in the shop
- **Author** – book authors (many-to-many with books)
- **Publisher** – publishing companies (one-to-many with books)
- **Client** – registered customers
- **Order** – book purchase orders created by clients

Relationships:

```

Author  ---< Book >--- Publisher
|
v
Order
|
v
Client

````

---

## Technologies Used

- **Java 25**
- **Spring Boot 3.x**
- **Spring Web** – REST API
- **Spring Data JPA** – ORM and database access
- **Hibernate** – JPA implementation
- **MapStruct** – object mapping between entities and DTOs
- **Lombok** – reduces boilerplate code
- **Maven** – dependency management

---

## Prerequisites

- **JDK 17 or later**
- **Maven 3.6+**

---

## Running the Application

Clone the repository:

```bash
git clone https://github.com/29121970t/bookwalker.git
cd bookwalker
````

Build the project:

```bash
mvn clean install
```

Run the application:

```bash
mvn spring-boot:run
```

The API will start on:

```
http://localhost:8080
```

---

## Example Request

Create a book:


---

### Report

## Bulk Orders And Transaction Demo

Bulk order creation was added as a business operation for a bookstore: one request can register several purchases at once.

Transactional endpoint:

```http
POST /orders/bulk
```

Non-transactional endpoint:

```http
POST /orders/bulk/no-transaction
```

Example payload with one invalid item in the middle:

```json
[
  {
    "client": 1,
    "books": [1, 2],
    "date": "2026-04-07T10:00:00"
  },
  {
    "client": 999999,
    "books": [1],
    "date": "2026-04-07T10:05:00"
  },
  {
    "client": 1,
    "books": [2],
    "date": "2026-04-07T10:10:00"
  }
]
```

How to see the difference in database state:

1. Check the number of orders before the request:

```sql
SELECT COUNT(*) FROM orders;
```

2. Send the payload to `POST /orders/bulk/no-transaction`.
3. Run `SELECT COUNT(*) FROM orders;` again.
4. You will see that the first valid order stayed in the database, even though the whole bulk request ended with an error.
5. Repeat the same payload with `POST /orders/bulk`.
6. Run `SELECT COUNT(*) FROM orders;` again.
7. The counter will not change, because `@Transactional` rolls back the whole bulk operation when one item fails.

Implementation notes:

- `OrderService#createBulkTransactional(...)` is atomic and uses `@Transactional`.
- `OrderService#createBulkNonTransactional(...)` intentionally works without `@Transactional` for demonstration.
- `OrderService` uses `Stream API` for processing the list of incoming orders and `Optional` for safe extraction of optional collections and dates.
