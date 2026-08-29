# AGENTS.md

## Repository

`starzz-boot` is a Java 17 Spring Boot 3.4.5 REST API for galaxies, constellations, stars, and users.

Use the repository contents as the source of truth. In particular, verify claims against `README.md`, `pom.xml`, the YAML configuration, source code, and tests rather than relying on stale Claude-specific notes.

## Working rules

- Preserve existing user changes and inspect `git status` before making edits.
- Before committing, review the complete diff and stage only files created or modified for the requested task; exclude pre-existing and unrelated files, including Claude artifacts and other user changes.
- Do not expose values from `.env`, especially database credentials or `JWT_SECRET`.
- Keep the layered architecture: controller -> service -> repository/entity, with DTOs and mappers between the API and persistence layers.
- Run relevant tests after code changes. The project requires Java 17; if the available JDK is older, report that clearly.
- Use the Maven wrapper (`mvnw.cmd` on Windows).
- Do not modify generated output under `target/` or `htmlReport/` unless explicitly requested.
- Do not delete, reset, or overwrite unrelated files without explicit authorization.

## GitHub commit requirement

Whenever the user asks Codex to commit changes to GitHub, Codex must update this `AGENTS.md` in the same change set before committing.

Each such update must add a dated entry under `## Commit history` containing:

- a concise summary of the change being committed;
- the validation performed and any known blockers;
- the commit hash after the commit is created, if available.

The update must be included in the commit itself. If a commit hash cannot be known until after committing, make the commit with the entry marked `pending`, then amend the commit to replace it with the actual hash when safe and authorized. Do not push to GitHub unless the user explicitly asks for a push.

## Commit history

### 2026-08-29 - Chapter 10: Enhancing our app with Swagger UI

- Added Swagger UI and OpenAPI documentation, public documentation endpoint rules, JWT Bearer authorization in Swagger UI, demo screenshots, and a demo admin-user migration.
- Validation: `mvnw.cmd test` passed with 116 tests, 0 failures, 0 errors, and 0 skipped; `git diff --check` passed. Docker validation remains blocked because Docker is not installed.
- Commit hash: 9042774.

### 2026-08-27 - Chapter 9: Enhancing our app with Docker containerization

- Added the MySQL database initialization setting, Docker Compose documentation, and container build configuration for the Spring Boot application.
- Validation: `git diff --check` and scoped review passed; live Docker validation was blocked because sudo requires an interactive password.
- Commit hash: 5e7554f (commit before the required hash-entry amend).

### 2026-08-27 - Chapter 9: Docker containerization

- Added Docker Compose documentation and configuration, a multi-stage Java 17 Dockerfile, Docker build exclusions, and refreshed project memory.
- Validation: `git diff --check` passed; Java 17.0.18 is available; Maven wrapper validation was blocked by its PowerShell bootstrap error; Docker Compose validation was blocked because Docker is not installed.
- Commit hash: c50e685 (commit before the required hash-entry amend).

### 2026-08-24 - Update project memory for Docker chapter planning

- Updated `memory/codex_project.md` with the current Chapter 8 Flyway status and the planned Chapter 9 Docker Compose direction.
- Validation: reviewed the scoped diff and confirmed unrelated working-tree files remain unstaged; Docker was not installed for Compose validation.
- Commit hash: 8c40f22.

### 2026-08-21 - Chapter 8: Enhancing our app with Flyway migrations

- Added Chapter 8 documentation covering Flyway migrations, schema history, configuration, and migration workflow.
- Added Flyway schema and seed migrations, enabled Flyway, and configured Hibernate schema validation for production and tests.
- Validation: git diff --check passed; Maven validation/tests have not been rerun for this commit.
- Commit hash: 2535d65.


### 2026-08-19 - Initial Codex repository guidance and memory

- Added this `AGENTS.md` with project conventions, commit-scope rules, and the GitHub-commit update requirement.
- Added `memory/codex_project.md` after auditing the README, source, configuration, schema, and tests.
- Repository audit completed; Maven test execution was blocked because the available JDK was 11 while the project requires Java 17.
- Commit hash: recorded in Git history after the initial commit.
