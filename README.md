# Bookwalker API

A simple RESTful API for managing a bookshop. Built with Spring Boot, this application provides endpoints to create, retrieve. 

## Endpoints
| Method | Endpoint           | Description                          | Request                   |
|--------|--------------------|--------------------------------------|---------------------------|
| GET    | ```/book/{id}```   | Retrieve a book by its ID            | None                      |
| GET    | ```/book?name=```  | Search books by name (partial match) | None                      |
| GET    | ```/book```        | Retrieve all books                   | None                      |
| POST   | ```/book ```       | Add a new book                       | ```BookCreateDto```  JSON |

## Technologies Used
- **Java 25**
- **Spring Boot 3.x**
- **Maven** – dependency management
- **MapStruct** – object mapping between entities and DTOs
- **Lombok**  – reduces boilerplate code

## Prerequisites
- JDK 17 or later
- Maven 3.6+

### [Report](https://github.com/29121970t/bookwalker/blob/main/java_1.pdf)