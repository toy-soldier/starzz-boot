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

### 2026-08-19 - Initial Codex repository guidance and memory

- Added this `AGENTS.md` with project conventions, commit-scope rules, and the GitHub-commit update requirement.
- Added `memory/codex_project.md` after auditing the README, source, configuration, schema, and tests.
- Repository audit completed; Maven test execution was blocked because the available JDK was 11 while the project requires Java 17.
- Commit hash: recorded in Git history after the initial commit.
