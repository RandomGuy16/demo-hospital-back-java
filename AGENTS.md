# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java/com/example/demo` contains the Spring Boot application code. Subpackages are organized by role: `controllers`, `services`, `repositories`, `models`, and `dto`.
- `src/main/resources` holds configuration such as `application.properties`.
- `src/test/java` contains JUnit 5 tests (currently a MockMvc-based API test).
- `docs/` contains the OpenAPI spec (`docs/openapi.yaml`) and architecture notes.
- Root docs include `README.md`, `PROJECT_GUIDELINES.md`, `CONTRIBUTING.md`, `SECURITY.md`, and `ROADMAP.md`.

## Build, Test, and Development Commands
- `./gradlew bootRun` starts the API locally on `http://localhost:8080`.
- `./gradlew clean build test` runs a full build and the test suite.
- `./gradlew test` runs tests only.

## Coding Style & Naming Conventions
- Java toolchain is set to 21 in `build.gradle.kts` (use a compatible JDK).
- Follow standard Java conventions and keep controllers thin (Controller → Service → Repository).
- Prefer DTOs for API boundaries; avoid exposing entities directly.
- Package naming follows `com.example.demo.<role>`.
- Indentation: 4 spaces; use standard Java formatting. No formatting tool is configured yet.

## Testing Guidelines
- Frameworks: JUnit 5 with Spring Boot test support; MockMvc for web-layer tests.
- Place tests under `src/test/java` and name classes `*Test` (example: `PatientTest`).
- Run locally with `./gradlew test` or `./gradlew clean build test` before opening a PR.

## Commit & Pull Request Guidelines
- Use Conventional Commits: `feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `build:`, `ci:`, `perf:`, `chore:`.
- Branch naming: `feat/<short-name>` for features, `fix/<short-name>` for bug fixes.
- PRs should include a clear description, linked issues, and screenshots when UI changes apply.
- Ensure CI is green and tests are updated for behavior changes.

## Security & Configuration Notes
- Do not report vulnerabilities via public issues; follow `SECURITY.md`.
- Configure environment via `src/main/resources/application.properties` or Spring `SPRING_*` variables (profiles: `dev`, `test`, `prod`).
