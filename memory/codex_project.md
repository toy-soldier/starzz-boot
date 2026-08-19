---
name: Codex Project Memory
description: Verified current state and working context for the starzz-boot repository
type: project
---

# starzz-boot — verified project memory

Last audited: 2026-08-19

## What this project is

`starzz-boot` is a Java 17 Spring Boot REST API for a legacy MySQL astronomy dataset. It manages galaxies, constellations, stars, and users. The package root is `com.sanjayrisbud.starzzboot`.

The repository README is a long, chapter-based walkthrough covering routes, JPA persistence, DTO mapping, validation, exception handling, unit tests, integration tests, BCrypt password hashing, and JWT/Spring Security. The current code is at the end of that walkthrough (Chapter 7: JWT), not at the earlier roadmap state.

## Build and configuration

- Maven wrapper: `mvnw` / `mvnw.cmd`.
- Maven project version: `com.sanjayrisbud:starzz-boot:0.0.1-SNAPSHOT`.
- Java release: 17.
- Spring Boot parent: 3.4.5.
- Main dependencies: Web, Data JPA, Validation, MySQL runtime driver, H2 test database, Spring Security, Spring Security Test, Lombok, spring-dotenv, and JJWT 0.12.6.
- Production settings are in `src/main/resources/application.yaml`.
- Production DB values come from `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`; JWT signing secret comes from `JWT_SECRET`. Do not expose `.env` values in logs or memory.
- The password-reset sentinel is configured as `app.security.password-reset-sentinel: resetRequired`; JWT expiration is 86,400,000 ms (one day).
- Admin usernames are configured under `app.admins` (`admin1`, `admin2`, `admin3` in the current checked-in config).
- Tests use H2 in MySQL compatibility mode with `create-drop` and seed data from `assets/load.sql`.
- `.env`, `target/`, IDE metadata, and the generated HTML coverage report are ignored.

## Architecture

The implementation follows Controller -> Service -> Repository -> JPA Entity, with explicit mapper and DTO layers.

- `controllers/`: `AuthController`, `GalaxyController`, `ConstellationController`, `StarController`, `UserController`.
- `services/`: CRUD/business logic for each resource plus `AuthService` and `JwtService`.
- `repositories/`: plain `JpaRepository` interfaces; `UserRepository` additionally has `findByName`.
- `models/`: `User`, `Galaxy`, `Constellation`, `Star`; relationships are lazy `ManyToOne` links and cascading removal from Galaxy -> Constellation -> Star.
- `mappers/`: manual conversion to summary/detail DTOs; summary DTOs avoid exposing nested relationships.
- `dtos/`: validated request DTOs, summary records, detail DTOs, error/token/password-reset responses.
- `exceptions/`: `GlobalExceptionHandler`, `ResourceNotFoundException`, and `PasswordResetRequiredException`.
- `config/` and `filters/`: `SecurityConfig`, `AdminProperties`, and `JwtAuthFilter`.

On updates, the resource services use `getEntity(newId, currentEntity)` helpers: a null ID clears the relationship; an unchanged ID reuses the current entity; a changed ID is loaded through the relevant service/repository and can produce a 404.

## HTTP API currently implemented

- `GET /galaxies`, `/galaxies/{id}`
- `POST /galaxies`, `PUT /galaxies/{id}`, `DELETE /galaxies/{id}`
- Equivalent routes for `/constellations` and `/stars`.
- `GET /users`, `/users/{id}`; `POST /users`; `PUT /users/{id}`.
- `PATCH /users/{id}/change-password`.
- `POST /login` returning `{ "token": "..." }`.

Create returns 201 with a `Location` header and detail body. Update returns 200 with a detail body. Delete returns 204. Resource-not-found and invalid input are handled as 404/400 JSON responses containing a message and timestamp.

There is no `DELETE /users/{id}` method in the current `UserController`; this is intentional in the README because the legacy foreign-key relationships should be preserved.

## Security behavior

`SecurityConfig` disables CSRF, form login, and HTTP Basic, and uses stateless sessions. `JwtAuthFilter` reads `Authorization: Bearer ...`, validates the signed token through `JwtService`, and installs a `ROLE_ADMIN` or `ROLE_USER` authority. Invalid/missing tokens leave the request unauthenticated.

Current access rules:

- Public: `POST /login`, `PATCH /users/*/change-password`, and GET routes for galaxies, constellations, and stars.
- `POST /users` requires role `ADMIN`.
- All other routes require authentication.
- Unauthenticated protected requests use a custom 401 entry point; authenticated users without permission receive 403 from Spring Security.

New users receive a BCrypt hash of the sentinel rather than a usable password. Login with the sentinel is rejected with 403 and a response containing the user ID, allowing the client to call the public change-password route. Wrong credentials produce 401 without revealing whether the username exists.

## Tests and verification

Tests include:

- Service unit tests for galaxy, constellation, star, user, auth, and JWT services.
- MVC controller tests for all five controllers using `@WebMvcTest` and `MockMvc`; security filters are disabled in these isolated tests.
- Full integration tests for all five controller areas using `@SpringBootTest`, `@AutoConfigureMockMvc`, H2, and seeded SQL. Auth integration tests exercise real login, JWT access, roles, invalid/missing tokens, and password-reset behavior.
- Shared `DtoFactory` and `EntityFactory` helpers are under `src/test/.../helpers`.

The README reports 99.7% IntelliJ line coverage, with only the application main method uncovered. This should be treated as documentation evidence until the suite is rerun in a Java 17 environment.

On 2026-08-19, `mvnw.cmd test` did not reach test execution: the machine used Oracle/OpenJDK 11 and Maven failed compiling tests with `release version 17 not supported`. Maven also emitted a warning that Lombok uses the nonstandard dependency scope `annotationProcessor`; investigate that separately if build modernization is requested.

## Claude artifact discrepancies

The checked-in `CLAUDE.md` and `memory/project_roadmap.md` are stale relative to the repository:

- They describe Spring Boot 4.0.2, while `pom.xml` and README use 3.4.5.
- They describe hardcoded DB credentials, while current production configuration uses environment variables and spring-dotenv.
- They say controller tests are still planned and that only service tests exist, while controller unit tests and integration tests are present.
- They omit the implemented BCrypt/JWT/security layer.

Use the code, `pom.xml`, current YAML files, tests, and README as the source of truth. Re-check this memory after material code or configuration changes.

## Useful commands

```text
./mvnw spring-boot:run
./mvnw clean install
./mvnw clean test
./mvnw clean test -Dtest=ConstellationServiceTest
./mvnw clean test -Dtest=ConstellationServiceTest#methodName
```

Before running Maven, verify that `java -version` reports Java 17 or newer and that the required DB environment variables are available for application runs. Integration tests use their own H2/test configuration.
