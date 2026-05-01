# Digital Banking Application

A Spring Boot application for managing digital banking operations, including customers, bank accounts (current and saving), and transactions.

## Prerequisites

- Java 17 or higher
- MySQL Server
- Maven

## Setup

1. **Database Setup**:
   Create the database in MySQL:
   ```sql
   CREATE DATABASE IF NOT EXISTS digital_banking;
   ```

2. **Environment Variables**:
   Create a `.env` file in the project root with the following variables:
   ```env
   DB_URL=jdbc:mysql://localhost:3306/digital_banking
   DB_USERNAME=your_username
   DB_PASSWORD=your_password
   ```

3. **Build and Run**:
   ```bash
   ./mvnw spring-boot:run
   ```

## Project Structure

- `entities`: JPA entities representing the database schema (Standardized to `snake_case`).
- `enums`: Account statuses and operation types.
- `repositories`: Spring Data JPA repositories.
- `services`: Business logic layer.
- `controllers`: REST APIs.

