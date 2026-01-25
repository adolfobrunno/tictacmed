# Project Guidelines

These guidelines define standards and best practices for this codebase. They are intentionally practical and aligned
with the current stack (Java 21, Spring Boot, Gradle, Testcontainers, MariaDB, RabbitMQ).

Contents

- Language Policy
- Clean Architecture
- DTOs over Entities
- Integration Tests
- Code Style and Conventions
- API Design
- Database and Migrations
- Observability
- Git, Branching and PRs
- Checklists

## 1. Language Policy

- All code, comments, commit messages, issues, PR titles/descriptions, and documentation MUST be in English.
- Use clear, concise wording. Prefer American English.

## 2. Clean Architecture

Structure the application by layers and feature modules to keep business rules independent from frameworks and delivery
mechanisms.

Recommended high-level package structure:

```
com.abba.tictacmed
  ├─ application          // Use cases (orchestrate domain, transactional boundaries)
  │   └─ <feature>
  │       ├─ command|query
  │       └─ service (if needed)
  ├─ domain               // Enterprise business rules, pure Java
  │   └─ <feature>
  │       ├─ model        // aggregates, value objects, domain events
  │       ├─ repository   // repository interfaces (ports)
  │       └─ service      // domain services (pure, side-effect free if possible)
  ├─ infrastructure       // Adapters and framework-driven code
  │   ├─ persistence      // JPA entities, Spring Data repositories (adapters)
  │   ├─ messaging        // RabbitMQ producers/consumers
  │   ├─ config           // Spring configs, security, bean wiring
  │   └─ mapper           // MapStruct or manual mappers
  └─ presentation         // Delivery mechanisms (REST controllers, GraphQL, etc.)
      └─ <feature>
          ├─ controller
          └─ dto         // request/response DTOs
```

Principles:

- Dependency direction: presentation/infrastructure -> application -> domain.
- Domain must not depend on Spring or any external framework.
- Application layer coordinates use cases and transactions; it depends on domain and ports, not on concrete
  infrastructure.
- Infrastructure implements ports (adapters). Keep JPA entities confined here.
- Presentation only exposes DTOs, performs input validation, and delegates to application.

## 3. DTOs over Entities

- Never expose JPA entities in controller responses or requests.
- Define RequestDTO and ResponseDTO classes under `presentation.<feature>.dto`.
- Map between DTOs and domain models in the application layer or via mappers in `infrastructure.mapper` using MapStruct
  or manual mapping.
- Validation: annotate request DTOs with Jakarta Validation (e.g., @NotNull, @Size) and validate at the controller
  boundary.
- Field naming: use `camelCase` in DTOs; translate snake_case from external systems in mapping if needed.
- Version DTOs when breaking changes are required (e.g., `v1`, `v2` packages or content negotiation).

Example patterns:

```java
// presentation.<feature>.dto
public record PatientRequestDTO(String name, String documentId) {
}

public record PatientResponseDTO(String id, String name) {
}

// domain.<feature>.model
public final class Patient { /* id, name, validations */
}

// application.<feature>
public class CreatePatientUseCase { /* execute(PatientRequestDTO) -> PatientResponseDTO */
}
```

## 4. Integration Tests

- Integration tests MUST be created for critical flows and infrastructure adapters.
- Use Spring Boot Test with Testcontainers to start MariaDB and RabbitMQ.
- Place tests under `src/test/java/...` mirroring the main package structure.
- Prefer `@SpringBootTest` with minimal context and slice tests where applicable (`@DataJpaTest`, `@WebMvcTest`) plus
  Testcontainers where necessary.
- Use `@Testcontainers` and static container fields to reuse containers across tests.
- Seed database via Flyway or test fixtures. Avoid relying on data from other tests.
- Tests MUST be deterministic, independent, and parallel-safe when possible.

Template example:

```java

@SpringBootTest
@Testcontainers
class PatientIntegrationTest {
    static MariaDBContainer<?> db = new MariaDBContainer<>("mariadb:latest");
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:latest");

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        db.start();
        rabbit.start();
        registry.add("spring.datasource.url", db::getJdbcUrl);
        registry.add("spring.datasource.username", db::getUsername);
        registry.add("spring.datasource.password", db::getPassword);
        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
    }

    @Test
    void should_create_and_fetch_patient() {
        // Arrange + Act + Assert using REST client or application service
    }
}
```

Minimum coverage for integration tests:

- Persistence adapters: repository queries, entity mappings.
- REST controllers: 2xx and error paths (validation, not found, conflicts).
- Messaging: publish/consume at least one end-to-end flow if used.

## 5. Code Style and Conventions

- Java 21, Spring Boot 3.x.
- Use meaningful names; avoid abbreviations.
- Methods: small, single-responsibility; prefer immutability.
- Null-safety: favor Optional and validation over nulls; never return null collections.
- Exceptions: use meaningful domain/application exceptions; map to HTTP via `@ControllerAdvice`.
- Logging: use SLF4J; no sensitive data in logs; structured messages.
- Configuration via `application.properties`/`application.yml`; externalize secrets.
- Prefer imports over fully qualified class names in code. Write `import java.time.ZonedDateTime;` and use
  `ZonedDateTime` in code, instead of `java.time.ZonedDateTime` inline. Exceptions: when avoiding import conflicts (
  e.g., `java.util.Date` vs `java.sql.Date`) or for static one-off references in docs/comments.

Recommended tools:

- Spotless or Checkstyle for formatting (Google Java Format or Spring style).
- MapStruct for DTO mappings where beneficial.

## 6. API Design

- Follow RESTful principles.
- Resource names are plural kebab-case (e.g., `/api/v1/patients`).
- Use standard HTTP status codes and problem details for errors.
- Pagination parameters: `page`, `size`, `sort`.
- Idempotency for PUT and safely designed POST where applicable.
- Version the API (`/api/v1/...`).

## 7. Database and Migrations

- All schema changes via Flyway migrations under `src/main/resources/db/migration`.
- No auto-DDL in production (`spring.jpa.hibernate.ddl-auto=validate`).
- Keep JPA entities in `infrastructure.persistence`; domain models are separate and persistence-agnostic.

## 8. Observability

- Expose health and readiness endpoints (`/actuator/health`).
- Add metrics for key use cases; use tracing if distributed systems are involved.

## 9. Git, Branching and PRs

- Branch naming: `feature/<short-name>`, `fix/<short-name>`, `chore/<short-name>`.
- Conventional Commits: `feat:`, `fix:`, `chore:`, `refactor:`, `test:`, `docs:`.
- PR requirements:
    - Description in English, reference issue.
    - Tests passing (unit + integration). CI required.
    - Include before/after or API changes.

## 10. Checklists

Definition of Done (per change):

- [ ] Code and docs in English.
- [ ] Clean Architecture respected (no forbidden dependencies).
- [ ] DTOs used at the API boundary; no entity leakage.
- [ ] Integration tests cover the change and pass locally.
- [ ] Migrations added/updated if DB schema changed.
- [ ] Logging and error handling verified.
- [ ] API versioning considered for breaking changes.

Integration Test Checklist:

- [ ] Containers start and stop deterministically.
- [ ] DB and brokers are isolated per test class or reused safely.
- [ ] Data setup and teardown are explicit.
- [ ] Tests assert both success and failure paths.

Adoption Note

- Existing code can be refactored incrementally. Start by carving out domain models, introducing DTOs for controllers,
  and adding integration tests for the most critical flows.
