# Contributing Guide

Thank you for your interest in improving the Hospital Organization API!

## Development Workflow

1. Fork the repo and create a feature branch:
   - `feat/<short-name>` for new features
   - `fix/<short-name>` for bug fixes
2. Keep changes small and focused; add/update tests for your changes.
3. Run the test suite locally before opening a PR:
   ```bash
   ./gradlew clean build test
   ```
4. Open a pull request (PR) with a clear description, screenshots (if applicable), and references to issues.
5. Ensure CI passes; reviewers will provide feedback.

## Commit Messages

Follow Conventional Commits:
- `feat: add patient search by MRN`
- `fix: handle null departmentId in mapper`
- `docs: add API usage examples`
- `test: add integration tests for appointments`

## Code Style

- Java 21 via the Gradle toolchain
- Prefer constructor injection
- Controller → Service → Repository separation; keep controllers thin
- Validate inputs with Bean Validation and return consistent errors
- Avoid leaking entities to the API; use DTOs

## Tests

- Unit tests for services and domain logic
- Web layer tests using MockMvc/WebTestClient
- Repository tests (consider Testcontainers later)

## Opening Issues

- Use the issue templates (when available) or include:
  - Summary, steps to reproduce, expected vs actual, logs/stack traces
  - Environment (OS, JDK, DB)

## Security Issues

Do not open public issues for security vulnerabilities. See SECURITY.md for reporting instructions.

## License

By contributing, you agree that your contributions will be licensed under the project’s chosen license.
