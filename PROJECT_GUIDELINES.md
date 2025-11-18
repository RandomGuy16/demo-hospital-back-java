# Project Guidelines

These guidelines describe how to build a robust, maintainable Spring Boot API for a hospital/health organization.

## Coding Standards

- Java 17+; adhere to standard Java conventions.
- Prefer immutability; make fields `private final` when possible.
- Use records or Lombok (optional) for DTOs to reduce boilerplate.
- Avoid static state and singletons except where explicitly safe.

## Packages

- `com.example.hospital` (later) with subpackages by feature: `patients`, `practitioners`, `appointments`, `departments`, `records`, `common`.
- Within a feature, use vertical slices: `api` (controllers), `application` (services/use-cases), `domain` (entities/aggregates), `infrastructure` (persistence, integrations).

## API Design

- Version your API under `/api/v1`.
- Resource-oriented routes: `/patients`, `/practitioners`, `/appointments`, `/departments`, `/medical-records`.
- Use nouns for resources and HTTP verbs for actions. Avoid verbs in endpoints.
- Support pagination via `page`, `size`, `sort`. Return `Page<T>`-like response with `items`, `page`, `size`, `total`.
- Filtering via explicit query parameters. Document in OpenAPI.
- Consistent timestamps in ISO 8601 UTC (`Instant`).

## Validation

- Use Jakarta Bean Validation annotations on request DTOs.
- Validate path and query parameters as well. Fail fast with 400 and a structured error body.

## Error Handling

- Provide a global `@ControllerAdvice` mapping exceptions to a standard error format:
  - `timestamp`, `traceId`, `code` (machine-readable), `message`, `details`.
- Use problem-detail alignment (RFC 9457) where practical.
- Never leak internal stack traces in production responses.

## Persistence

- Start with an in-memory H2 database for dev; use PostgreSQL in prod.
- Use Spring Data JPA for convenience or switch to JDBC/QueryDSL/MyBatis depending on complexity/performance needs.
- Migrations via Flyway or Liquibase.

## Security

- Phase 1: open endpoints for local dev only.
- Phase 2: OAuth2/OIDC (Keycloak/Okta), audience/issuer checked, scopes/roles by resource. Method-level authorization with `@PreAuthorize`.
- Input sanitization and output encoding. Add rate limiting for public endpoints.
- Store secrets outside VCS. Prefer environment variables or a secrets manager.

## Observability

- Logging:
  - Dev: human-readable.
  - Non-dev: JSON logs with correlation IDs.
- Metrics via Micrometer, Prometheus endpoint `/actuator/prometheus`.
- Tracing via OpenTelemetry.

## Testing Strategy

- Unit tests: domain and services with JUnit 5 + Mockito.
- API tests: Spring MockMvc/WebTestClient.
- Integration tests: slice tests for repositories with Testcontainers (PostgreSQL).
- Contract tests against OpenAPI using tools like spring-cloud-contract or openapi-diff in CI.
- Aim for >80% critical path coverage.

## Documentation

- Maintain `docs/openapi.yaml` as the source of truth.
- Keep `README.md` updated for run/build instructions.
- Architecture decisions tracked as ADRs (optional) in `docs/adr/`.

## Git Workflow

- Branching: `main` (protected), feature branches `feat/<short-name>`, fix branches `fix/<short-name>`.
- Conventional Commits:
  - `feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `build:`, `ci:`, `perf:`, `chore:`
- PRs require review, CI green, tests added/updated.

## Code Style Tools (optional)

- Checkstyle or Spotless for formatting.
- ErrorProne for static analysis.
- SonarQube for quality gates.

## Performance & Scalability

- Paginate all list endpoints.
- Avoid N+1 queries; use fetch joins or batch fetching.
- Cache hot, stable reads (e.g., departments) with Spring Cache + Caffeine/Redis.

## Deployment

- Containerize with a distro-less base image.
- Health endpoints: `/actuator/health`, readiness/liveness separated.
- Config via env vars; twelve-factor principles.

## Data Privacy & Compliance

- Consider healthcare privacy regulations in your jurisdiction (e.g., HIPAA, GDPR). Limit PII; apply data minimization.
- Audit trails for sensitive access.
