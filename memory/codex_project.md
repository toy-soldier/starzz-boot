---
name: Codex Project Memory
description: Verified current state and working context for the starzz-boot repository
type: project
---

## Current session handoff

- Chapter 8 (Flyway migrations) is committed in `7119aa2`.
- Chapter 9 (Docker containerization) is now documented in `README.md`. Current uncommitted changes update `.dockerignore`, `.env.example`, `Dockerfile`, `compose.yaml`, and the README.
- The Docker design uses separate Spring Boot and MySQL services, a multi-stage Java 17 image build, a named `mysql-data` volume, a MySQL health check, and `depends_on: condition: service_healthy`.
- Java 17.0.18 is available. However, `mvnw.cmd` currently fails in its PowerShell bootstrap with `Cannot index into a null array`, so Maven validation and tests could not run through the required wrapper.
- Docker is not installed in the current environment, so Compose configuration and live-stack validation could not run.
- Review note resolved: although `compose.yaml` does not set `MYSQL_DATABASE`, `.env` contains `createDatabaseIfNotExists=true` in `DOCKER_MYSQL_URL`, so the JDBC connection can create the `starzz` database before Flyway runs. The actual secret-bearing `.env` values remain intentionally undisclosed.
- Preserve unrelated uncommitted artifacts: `.claude/`, `CLAUDE.md`, and `memory/project_roadmap.md`.

## Verified project state

Last audited: 2026-08-27

`starzz-boot` is a Java 17 Spring Boot 3.4.5 REST API for a legacy MySQL astronomy dataset. It manages galaxies, constellations, stars, and users. The package root is `com.sanjayrisbud.starzzboot`.

The README is a chapter-based walkthrough covering routes, JPA persistence, DTO mapping, validation, exception handling, unit and integration tests, BCrypt password hashing, JWT/Spring Security, Flyway migrations, and Docker containerization. The source code is at the Flyway stage; Docker changes are currently documentation/configuration changes in the working tree.

## Build and configuration

- Maven wrapper: `mvnw` / `mvnw.cmd`; use `mvnw.cmd` on Windows.
- Maven coordinates: `com.sanjayrisbud:starzz-boot:0.0.1-SNAPSHOT`.
- Java release: 17. Spring Boot parent: 3.4.5.
- Main dependencies include Web, Data JPA, Validation, MySQL, H2 tests, Spring Security, Spring Security Test, Lombok, spring-dotenv, Flyway, and JJWT 0.12.6.
- Production database values use `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`; JWT signing uses `JWT_SECRET`. Never expose `.env` values.
- Tests use H2 in MySQL compatibility mode and run the same Flyway migrations as production.
- Docker uses `mysql:9.6.0`, fixed host mappings `3306:3306` and `8080:8080`, `DOCKER_MYSQL_*` variables, and `DOCKER_MYSQL_URL` for the app in-network JDBC URL.
- `Dockerfile` builds with Maven/JDK 17 and runs the JAR on `eclipse-temurin:17-jre` as a non-root `spring` user. `.dockerignore` excludes secrets, build output, IDE metadata, and documentation artifacts.
- `.env`, `target/`, IDE metadata, and generated HTML coverage output are ignored.

## Architecture and API

The implementation follows Controller -> Service -> Repository -> JPA Entity, with explicit DTO and mapper layers. Controllers cover authentication plus galaxies, constellations, stars, and users. Relationships are lazy `ManyToOne` links, with cascading removal from Galaxy -> Constellation -> Star.

Implemented routes include CRUD for galaxies, constellations, and stars; user listing, lookup, creation, update, and password change; and `POST /login`. There is intentionally no user-delete route because legacy foreign-key relationships should be preserved.

Security is stateless JWT with `ROLE_ADMIN` and `ROLE_USER`. Public routes are login, password change, and GET routes for astronomy resources. Creating users requires ADMIN; other protected routes require authentication. New users receive a BCrypt hash of the configured `resetRequired` sentinel.

## Tests and verification

Tests include service unit tests, MVC controller tests, and full Spring Boot integration tests covering all controller areas, authentication, JWT access, roles, invalid tokens, and password-reset behavior. The README's 99.7% IntelliJ coverage figure is documentation evidence until the suite can be rerun.

Current verification: `git diff --check` passed; Java 17 is available; Maven wrapper validation was blocked by the wrapper bootstrap error; Docker validation was blocked because Docker is not installed.

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
