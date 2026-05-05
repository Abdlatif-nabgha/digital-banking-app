<div align="center">
  <h1>🏦 Digital Banking Enterprise API</h1>
  <p><strong>A robust, secure, and scalable backend solution for digital banking operations.</strong></p>

  [![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
  [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.14-brightgreen.svg)](https://spring.io/projects/spring-boot)
  [![Spring Security](https://img.shields.io/badge/Security-JWT-blue.svg)](https://spring.io/projects/spring-security)
  [![MySQL](https://img.shields.io/badge/Database-MySQL-blue.svg)](https://www.mysql.com/)
</div>

<hr>

## 📖 Overview

The **Digital Banking API** is an enterprise-level backend application designed to manage core banking operations. Built with modern Java standards and the Spring Boot ecosystem, it provides a comprehensive suite of features for managing customers, different types of bank accounts (Current and Savings), and secure transaction processing. 

This project demonstrates best practices in backend development, including clean architecture, secure authentication, role-based access control, and robust exception handling, making it an excellent showcase of professional software engineering principles.

---

## ✨ Key Features

- **🛡️ Advanced Security & Authentication:** 
  - Implementation of Spring Security.
  - Stateless authentication using **JSON Web Tokens (JWT)**.
  - Role-Based Access Control (RBAC) to differentiate between Admin and User privileges.
- **📧 Email Verification Workflow:** Secure account activation through automated email verification upon user registration.
- **👨‍💼 Admin Dashboard APIs:** Dedicated `AdminController` for elevated privileges and global oversight of banking operations.
- **👥 Customer Management:** Comprehensive RESTful endpoints for onboarding and managing customer profiles.
- **💳 Account & Transaction Management:** 
  - Support for distinct account types: `CurrentAccount` and `SavingAccount`.
  - Secure processing of deposits, withdrawals, and account-to-account transfers.
- **🏗️ Enterprise Architecture:**
  - Utilization of Data Transfer Objects (DTOs) for secure data exposure.
  - Automated object mapping using **MapStruct**.
  - Centralized global exception handling (`GlobalExceptionHandler`) for standardized API error responses.
  - Interactive API documentation using **Swagger/OpenAPI**.

---

## 🛠️ Technology Stack

| Category | Technology |
| :--- | :--- |
| **Core Language** | Java 21 |
| **Framework** | Spring Boot 3.5.14 |
| **Security** | Spring Security, JJWT (0.12.6) |
| **Persistence** | Spring Data JPA, Hibernate |
| **Database** | MySQL |
| **Mappers & Utilities** | MapStruct (1.6.3), Lombok, Dotenv |
| **API Documentation** | Springdoc OpenAPI (Swagger UI) |
| **Build Tool** | Maven |

---

## 📂 Project Architecture

The project adheres to a standard layered architecture to ensure separation of concerns and maintainability:

```text
src/main/java/com/nabgha/digitalbanking/
├── config/        # Application and Security configurations
├── controllers/   # REST API endpoints (Admin, Auth, Customer, etc.)
├── dtos/          # Data Transfer Objects for API requests/responses
├── entities/      # JPA domain models (Customer, AppUser, BankAccount, Operation)
├── enums/         # Enumerations (AccountStatus, OperationType, Roles)
├── exceptions/    # Custom business exceptions and GlobalExceptionHandler
├── mappers/       # MapStruct interfaces for Entity <-> DTO conversion
├── repositories/  # Spring Data JPA interfaces
├── security/      # JWT filters, providers, and authentication logic
└── services/      # Core business logic and use case implementations
```

---

## 🚀 Getting Started

Follow these instructions to set up the project locally.

### Prerequisites
- **Java 21** or higher
- **MySQL Server** running locally or remotely
- **Maven** (or use the provided Maven wrapper)

### 1. Database Setup
Log in to your MySQL instance and execute the following command to create the database:
```sql
CREATE DATABASE IF NOT EXISTS digital_banking;
```

### 2. Environment Configuration
Create a `.env` file in the root directory of the project (where the `pom.xml` is located) and configure your environment variables:

```env
DB_URL=jdbc:mysql://localhost:3306/digital_banking
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password
# Add your email configuration for verification
MAIL_USERNAME=your_email@example.com
MAIL_PASSWORD=your_email_app_password
# JWT Secret Key
JWT_SECRET=your_secure_random_base64_secret_key_here
```

### 3. Build and Run
Open your terminal, navigate to the project directory, and run the application using the Maven wrapper:

```bash
# For Unix/macOS
./mvnw clean install
./mvnw spring-boot:run

# For Windows
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

---

## 📚 API Documentation

Once the application is running, you can explore and test the APIs via the interactive **Swagger UI**.

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

*(Note: The port `8080` is the default. Adjust if specified differently in your `application.properties`/`.env`).*

---

## 🤝 Contact & Author

Developed by **Abdlatif Nabgha**.

This project was developed to demonstrate proficiency in enterprise-grade Java backend development, security implementations, and API design. Feel free to explore the source code to review the implementation details and architectural decisions.
