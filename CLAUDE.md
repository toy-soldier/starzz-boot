# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**starzz-boot** is a Spring Boot REST API managing a galaxy/constellation/star database. Stack: Java 17, Spring Boot 4.0.2, Spring Data JPA, MySQL, Lombok, JUnit 5 + Mockito.

## Commands

```bash
# Run the application
./mvnw spring-boot:run

# Build
./mvnw clean install

# Run all tests
./mvnw clean test

# Run a single test class
./mvnw clean test -Dtest=ConstellationServiceTest

# Run a single test method
./mvnw clean test -Dtest=ConstellationServiceTest#methodName
```

**Database:** MySQL at `localhost:3309`, database `starzz`, credentials root/root (see `src/main/resources/application.yaml`).

## Architecture

Strict layered architecture: **Controller → Service → Repository → Entity**

- **`controllers/`** — `@RestController` classes, one per resource (`Galaxy`, `Constellation`, `Star`, `User`). Return `ResponseEntity<T>` with appropriate HTTP status codes (200/201/204/404).
- **`services/`** — Business logic. Each service has a `getEntity(newId, currentEntity)` helper that resolves whether to look up a new entity from the repository or reuse the existing one (used during update operations).
- **`repositories/`** — `JpaRepository` extensions; no custom SQL needed.
- **`models/`** — JPA entities with Lombok (`@Builder`, `@Getter`, `@Setter`). Relationships: Galaxy → Constellation → Star (one-to-many with cascade delete, lazy loading).
- **`mappers/`** — Convert entities to `SummaryDto` (lightweight) or `DetailsDto` (includes nested relationships).
- **`dtos/`** — Request DTOs use `@NotNull`/`@NotBlank` validation. Summary DTOs are Java records; Detail DTOs use `@Builder`.
- **`exceptions/`** — `ResourceNotFoundException` (→ HTTP 404) caught by `GlobalExceptionHandler` (`@RestControllerAdvice`), which also handles validation errors (→ HTTP 400).

## Testing Patterns

Tests live in `src/test/java/` mirroring the main package structure. Only service-layer tests exist (no controller or repository tests).

```java
@ExtendWith(MockitoExtension.class)
class ConstellationServiceTest {
    @Mock ConstellationRepository constellationRepository;
    @Spy  ConstellationMapper constellationMapper = new ConstellationMapper(...); // real instance, not mock
    @InjectMocks ConstellationService constellationService;
}
```

- Use `@Mock` for repositories and external services; use `@Spy` for mappers (they are deterministic and lightweight).
- Test data is built via `EntityFactory` and `DtoFactory` helper classes in `src/test/java/.../helpers/`.
- Use `assertEquals`/`assertNull`/`assertThrows` from JUnit 5 and `verify()`/`when()` from Mockito.
