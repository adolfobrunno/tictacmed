# TicTacMed

A simple medication reminder API built with Spring Boot 3. It lets you:

- Register patients
- Create medication schedules for patients
- Query the next upcoming administrations for a patient
- Explore the API using Swagger UI

This repository demonstrates a clean architecture split across domain, application (use cases), and infrastructure
layers. It uses MySQL (via Flyway for migrations) and RabbitMQ (mocked in tests and wired via configuration for real
usage).

## Tech stack

- Java 21
- Spring Boot 3 (Web, Data JPA, AMQP)
- MySQL + Flyway (schema migrations)
- Testcontainers (integration tests)
- Swagger/OpenAPI via springdoc-openapi

## Getting started

### Prerequisites

- JDK 21
- Docker (for local MySQL and RabbitMQ using Docker Compose)
- Gradle (Wrapper included)

### Start infrastructure with Docker Compose

A minimal setup is provided to run MySQL and RabbitMQ locally:

```bash
# from repository root
docker compose up -d
```

By default, the application expects:

- MySQL at jdbc:mysql://localhost:3306/tictacmed (user: root, password: secret)
- RabbitMQ at default ports (credentials via environment variables if needed)

You can change these in src/main/resources/application.yml.

### Run the application

```bash
# Windows PowerShell
./gradlew.bat bootRun

# Linux/macOS
./gradlew bootRun
```

Once running, the API will be available at http://localhost:8080.

### Explore the API (Swagger UI)

Open http://localhost:8080/swagger-ui.html to view and try the endpoints.

## API overview

Base path: /api

- POST /api/patients
    - Registers a patient.
    - Request: { "name": "John Doe", "contact": "+123456789" }
    - Response: 201 Created with patient data (id, name, contact)

- POST /api/schedules
    - Creates a medication schedule for a patient.
    - Request:
      {
      "patientId": "<UUID>",
      "medicineName": "Ibuprofen",
      "startAt": "2025-01-01T10:00:00Z",
      "endAt": "2025-01-01T14:00:00Z",
      "frequency": "10m"
      }
    - Notes:
        - frequency accepts a friendly format like: 30s, 10m, 2h, 1d (combinators like 1h30m are also supported by
          concatenation: e.g., "1h30m"). If a pure number is provided, it is treated as seconds for backward
          compatibility.

- GET /api/schedules/next?patientId=<UUID>&from=<ISO>&to=<ISO>
    - Returns the next due administration for each schedule of the given patient within the [from..to] window.
    - Default: from=now, to=from+1 day.

## Persistence & schema

- Flyway migration scripts are in src/main/resources/db/migration (starting with V1__init.sql)
- MySQL driver: com.mysql.cj.jdbc.Driver
- JPA settings use UTC for timestamps.

## Configuration

Key properties (see application.yml):

- spring.datasource.url: jdbc:mysql://localhost:3306/tictacmed
- spring.datasource.username: root
- spring.datasource.password: secret
- tictacmed.scheduler.enabled: true/false (controls periodic reminder job)
- tictacmed.whatsapp.enabled: true/false (example of a notification channel)

## Tests

This project uses JUnit and Spring Boot test support. Some tests use Testcontainers for MySQL and RabbitMQ.

Run tests:

```bash
# Windows PowerShell
./gradlew.bat test

# Linux/macOS
./gradlew test
```

## Project layout

- domain: Core business models and repositories
- application: Use cases (services) and commands/results
- infrastructure: Web controllers, persistence adapters/entities, messaging, scheduling, configuration

## Notes

- The scheduling frequency is stored internally as seconds, but the API accepts a friendly duration string.
- The scheduler job can send notifications via pluggable channels (e.g., WhatsApp) when enabled and configured.

## License

This project is provided as-is for demonstration purposes. Add your preferred license text here.