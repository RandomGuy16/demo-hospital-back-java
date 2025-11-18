# Roadmap

A phased plan to evolve this repository into a robust Hospital Organization API.

## Phase 0 — Foundations (now)
- Minimal Spring Boot app running locally ✓
- Documentation: README, Guidelines, Security, Contributing ✓
- OpenAPI blueprint draft (docs/openapi.yaml) → create and iterate

## Phase 1 — Core Patient & Practitioner Domain
- Patients API (CRUD, search by MRN, pagination)
- Practitioners API (CRUD, specialties)
- Departments API (CRUD)
- Global error handling with Problem Details alignment
- Input validation (Bean Validation)
- DTO mapping (MapStruct) [optional]
- In-memory H2 persistence; Flyway baseline
- Basic API tests (MockMvc) and service unit tests

## Phase 2 — Scheduling & Access Control
- Appointments API (create, reschedule, cancel)
- Availability checking rules
- OAuth2/OIDC authentication (Keycloak/Okta)
- Role-based authorization (admin, clinician, staff)
- Audit logging for sensitive operations
- Observability: Micrometer + Prometheus, basic Grafana dashboard

## Phase 3 — Medical Records & Integrations
- Medical Records API (encounters, diagnoses, medications)
- Attachments/Document management (S3/minio)
- Import/export: basic HL7 FHIR mapping for Patients (read-only pilot)
- Background jobs (Spring Batch) for data imports
- Postgres with Testcontainers in tests; production-ready Docker images

## Phase 4 — Reliability & Scale
- Caching (Caffeine/Redis) for reference data
- Idempotency keys for mutating requests
- Rate limiting and API keys for service-to-service access
- Canary deployments and blue/green support
- Performance testing suite (k6/Gatling)

## Phase 5 — Compliance & Hardening
- Data retention policies and PII minimization
- Encryption at rest where applicable, KMS integration
- DLP scanning for logs and storage
- Disaster recovery runbooks and chaos experiments

## Non-Functional Backlog
- Migrate to module-centric package `com.example.hospital.*`
- Add Spotless/Checkstyle, ErrorProne, and SonarQube
- ADRs for major decisions in `docs/adr/`

## Milestones & Checkpoints
- 1.0.0-alpha: Core CRUD for Patients/Practitioners/Departments, H2, tests
- 1.0.0-beta: Appointments + AuthZ/AuthN, Postgres, observability
- 1.0.0: Medical Records, integrations, performance/scalability

## Immediate Next Steps
- Implement `/api/v1/patients` with in-memory repo and tests
- Add global exception handler and standard error model
- Draft security config with open endpoints for dev
