# Hospital Organization API (Spring Boot)

A starter Spring Boot project for a hospital/health organization API. This repository begins with a minimal endpoint and provides a roadmap, guidelines, and an OpenAPI blueprint to evolve into a robust REST API covering core healthcare domains (patients, practitioners, appointments, departments, and medical records).

## Quick Start

Prerequisites:
- Java 21
- Gradle Wrapper (included) or Gradle 8+

The project uses the Gradle Java toolchain and will resolve JDK 21 automatically when needed.
If your system `java` is newer or incompatible with the Gradle wrapper, run Gradle once with a local JDK 21 and then pin the daemon JVM with `./gradlew updateDaemonJvm`.

Run the application:

```bash
make db-up
./gradlew bootRun
```

Or with `make`:

```bash
make dev
```

Build and run tests:

```bash
./gradlew clean build test
```

Useful shortcuts:

```bash
make test
make build
make clean
```

The app starts on http://localhost:8080.
The local PostgreSQL database is `hospitaldb` on `localhost:5432`.

Health check/demo endpoint:
- GET `http://localhost:8080/api` → `{ "message": "Hello World!" }`

## Project Structure

- `src/main/java/com/example/demo` — Spring Boot app and controllers
- `src/main/resources` — configuration
- `src/test/java` — tests
- `docs/openapi.yaml` — Initial OpenAPI 3.1 specification and blueprint
- `README.md` — this file
- `PROJECT_GUIDELINES.md` — coding, API, testing standards
- `CONTRIBUTING.md` — how to contribute
- `SECURITY.md` — vulnerability disclosure and security practices
- `ROADMAP.md` — phased plan to evolve into a full hospital API
- `docs/ARCHITECTURE.md` — high-level design and domain model

## API Blueprint (OpenAPI)

You can explore the initial specification at `docs/openapi.yaml`. It includes core resources (Patients, Practitioners, Appointments, Departments, MedicalRecords) with example requests/responses and standard conventions (versioning, pagination, error model).

You can render the spec with a local container of Swagger UI:

```bash
docker run -p 8081:8080 \
  -e SWAGGER_JSON=/openapi.yaml \
  -v $(pwd)/docs/openapi.yaml:/openapi.yaml \
  swaggerapi/swagger-ui
```

Open http://localhost:8081 to view the docs.

## Local Environment and Configuration

- Default port: `8080`
- Configure via `src/main/resources/application.properties` or environment variables (Spring Boot supports `SPRING_APPLICATION_JSON` and `SPRING_*` envs).
- Default database: PostgreSQL `hospitaldb` with username `hospital` and password `hospital`

Common properties:
- `server.port=8080`
- `spring.profiles.active=dev`
- `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/hospitaldb`
- `SPRING_DATASOURCE_USERNAME=hospital`
- `SPRING_DATASOURCE_PASSWORD=hospital`

Use Spring Profiles for environment-specific configuration: `dev`, `test`, `prod`.

Start only the database:

```bash
make db-up
```

Start the API and PostgreSQL together in Docker:

```bash
make docker-dev
```

## Testing

- Unit tests: JUnit 5
- HTTP tests: You can add Spring MockMvc/WebTestClient tests under `src/test/java`

Run tests:

```bash
./gradlew test
```

## Error Handling and Response Format

- Standard error structure is proposed in the OpenAPI spec: `ErrorResponse` with `code`, `message`, `details`, `traceId`.
- Adopt Problem Details (RFC 9457) alignment where feasible.

## Observability

- Logging: use structured logging (JSON) in non-dev environments.
- Correlation IDs: propagate `traceId`/`X-Request-ID`.
- Metrics: Micrometer + Prometheus (add later per ROADMAP).

## Security

- Start unauthenticated for dev.
- Add OAuth2/OIDC (Keycloak/Okta) and method-level authorization in Phase 2 (see ROADMAP).
- Validate input rigorously; never trust client data.

## Contributing

Please see `CONTRIBUTING.md` for guidelines and the development workflow.

## License

Specify a license for your project (e.g., MIT, Apache-2.0). Add a `LICENSE` file if needed.
