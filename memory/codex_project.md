---
name: Codex Project Memory
description: Verified current state and working context for the starzz-boot repository
type: project
---

## Current session handoff

- Chapter 8 (Flyway migrations) is committed in `7119aa2`.
- Chapter 9 (Docker containerization) is documented and committed in `367ed9a`.
- Chapter 10 is the final chapter, titled `Enhancing our app with Swagger UI`, and is implemented in two stages.
- Stage 1: add OpenAPI generation, Swagger UI, and public documentation endpoints without JWT integration.
- Stage 2: add the JWT Bearer security scheme and Swagger UI Authorize button, using the existing `POST /login` endpoint for manual token retrieval. This is implemented in `OpenApiConfig.java`.
- Chapter 10 Stage 1 is committed as `7768125`. The Maven wrapper also contains a separate, intentionally uncommitted PowerShell compatibility fix in `mvnw.cmd`. Preserve unrelated untracked artifacts: `.claude/`, `CLAUDE.md`, and `memory/project_roadmap.md`.
- The Docker design uses separate Spring Boot and MySQL services, a multi-stage Java 17 image build, a named `mysql-data` volume, a MySQL health check, and `depends_on: condition: service_healthy`.
- Java 17.0.18 is available. The separate PowerShell compatibility fix in `mvnw.cmd` allows Maven validation through the wrapper when Maven's cache is redirected to a writable temporary directory.
- Docker is not installed in the current environment, so Compose configuration and live-stack validation could not run.
- Review note resolved: although `compose.yaml` does not set `MYSQL_DATABASE`, `.env` contains `createDatabaseIfNotExist=true` in `DOCKER_MYSQL_URL`, so the JDBC connection can create the `starzz` database before Flyway runs. The actual secret-bearing `.env` values remain intentionally undisclosed.

## Verified project state

Last audited: 2026-08-29

`starzz-boot` is a Java 17 Spring Boot 3.4.5 REST API for a legacy MySQL astronomy dataset. It manages galaxies, constellations, stars, and users. The package root is `com.sanjayrisbud.starzzboot`.

The README is a chapter-based walkthrough covering routes, JPA persistence, DTO mapping, validation, exception handling, unit and integration tests, BCrypt password hashing, JWT/Spring Security, Flyway migrations, Docker containerization, and the two-stage Swagger UI enhancement. Swagger/OpenAPI paths are permitted in `SecurityConfig`, while the JWT Bearer scheme is defined in `OpenApiConfig`.

## Build and configuration

- Maven wrapper: `mvnw` / `mvnw.cmd`; use `mvnw.cmd` on Windows.
- Maven coordinates: `com.sanjayrisbud:starzz-boot:0.0.1-SNAPSHOT`.
- Java release: 17. Spring Boot parent: 3.4.5.
- Main dependencies include Web, Data JPA, Validation, MySQL, H2 tests, Spring Security, Spring Security Test, Lombok, spring-dotenv, Flyway, and JJWT 0.12.6.
- Production database values use `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`; JWT signing uses `JWT_SECRET`. Never expose `.env` values.
- Tests use H2 in MySQL compatibility mode and run the same Flyway migrations as production.
- Docker uses the official `mysql:9.6.0` image, fixed host mappings `3306:3306` and `8080:8080`, `DOCKER_MYSQL_*` variables, and `DOCKER_MYSQL_URL` for the app's in-network JDBC URL.
- `Dockerfile` builds with Maven/JDK 17 and runs the JAR on `eclipse-temurin:17-jre` as a non-root `spring` user. `.dockerignore` excludes secrets, build output, IDE metadata, and documentation artifacts.
- `.env`, `target/`, IDE metadata, and generated HTML coverage output are ignored.

## Architecture and API

The implementation follows Controller -> Service -> Repository -> JPA Entity, with explicit DTO and mapper layers. Controllers cover authentication plus galaxies, constellations, stars, and users. Relationships are lazy `ManyToOne` links, with cascading removal from Galaxy -> Constellation -> Star.

Implemented routes include CRUD for galaxies, constellations, and stars; user listing, lookup, creation, update, and password change; and `POST /login`. There is intentionally no user-delete route because legacy foreign-key relationships should be preserved. `PATCH /users/{id}/change-password` is public for initial sentinel-password resets.

Security is stateless JWT with `ROLE_ADMIN` and `ROLE_USER`. Public routes are login, password change, and GET routes for astronomy resources. Creating users requires ADMIN; other protected routes require authentication. New users receive a BCrypt hash of the configured `resetRequired` sentinel.

## Tests and verification

Tests include service unit tests, MVC controller tests, and full Spring Boot integration tests covering all controller areas, authentication, JWT access, roles, invalid tokens, and password-reset behavior. The README's 99.7% IntelliJ coverage figure is documentation evidence until the suite can be rerun.

Current verification: Java 17.0.18 is available; `mvnw.cmd test` passed with 116 tests, 0 failures, 0 errors, and 0 skipped after redirecting Maven's cache to a writable temporary directory. Docker validation was blocked because Docker is not installed. Existing Lombok, Maven model, Spring, and PowerShell profile warnings remain unrelated to this change.

## Source-of-truth guidance

Use `README.md`, `pom.xml`, YAML configuration, source, migrations, and tests as authoritative. `CLAUDE.md` and `memory/project_roadmap.md` may be stale and should not override the repository. Do not expose credentials or JWT secrets, modify `target/` or `htmlReport/`, or stage unrelated user changes.

## Useful commands

```text
./mvnw spring-boot:run
./mvnw clean install
./mvnw clean test
./mvnw clean test -Dtest=ConstellationServiceTest
docker compose up --build
docker compose down
```
