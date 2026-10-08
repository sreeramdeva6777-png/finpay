# FinPay

FinPay is a full-stack digital wallet application built with Java and Spring Boot.

The main idea behind the project was to build something closer to a real backend system rather than just a basic CRUD application. It supports user authentication, wallet operations, money transfers, transaction history, and a few reliability features that are important when dealing with financial operations.

## What FinPay Can Do

- User registration and login
- JWT-based authentication
- Create and manage a wallet
- Add money to a wallet
- Deduct money from a wallet
- Transfer money between users
- View transaction history
- Prevent duplicate transactions using idempotency keys
- Handle concurrent wallet updates using optimistic locking
- Validate requests and handle application errors
- Store passwords securely using BCrypt

## Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- Maven

### Database
- MySQL
- Flyway

### Frontend
- React
- JavaScript

### Testing
- JUnit 5
- Mockito
- Spring Boot Integration Testing
- Postman

### DevOps
- Docker
- Docker Compose
- GitHub Actions

## Project Structure

```text
FinPay
│
├── finpay-backend
│   ├── src
│   │   ├── main
│   │   │   └── java
│   │   └── test
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── pom.xml
│
├── finpay-frontend
│   └── React application
│
└── .github
    └── workflows
        └── ci.yml