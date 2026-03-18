# Architecture Overview

This document outlines a pragmatic architecture for the Hospital Organization API.

## Goals
- Keep services simple and maintainable
- Support incremental growth from monolith to modular services
- Prioritize correctness, security, and observability

## High-Level Design
- Monolithic Spring Boot application with modular package structure (feature slices)
- Clear separation of layers:
  - API (controllers, request/response DTOs, validation)
  - Application (services/use-cases, transactions, orchestration)
  - Domain (entities/aggregates, business rules)
  - Infrastructure (persistence, clients, configuration)

## Domain Model (Initial)
- Patient: id (UUID), MRN, first/last name, DOB, gender, contacts, address, timestamps
- Practitioner: id, name, specialties, departmentId, contacts
- Department: id, name, description
- Appointment: id, patientId, practitionerId, departmentId, start, end, status
- MedicalRecord: id, patientId, encounters, diagnoses, medications (future expansion)

## Persistence
- Phase 1: PostgreSQL for local dev via Docker Compose
- Phase 2+: PostgreSQL with Flyway migrations
- Repositories via Spring Data JPA or alternative (JDBC/QueryDSL) as complexity demands

## API Conventions
- Base path: `/api/v1`
- Consistent pagination, sorting, and filtering
- Standard error response (see OpenAPI spec)
- Idempotency keys for mutating endpoints (later)

## Security
- Phase 1: open for local development
- Phase 2: OAuth2/OIDC (Keycloak/Okta) with role-based access
- Fine-grained method-level authorization via annotations

## Observability
- Logging: human-readable dev, JSON in non-dev with correlation IDs
- Metrics: Micrometer + Prometheus
- Tracing: OpenTelemetry integration

## Deployment
- Containerized application; health/readiness probes via Spring Boot Actuator
- Externalize configuration via environment variables; twelve-factor compliant

## Evolution Path
- Extract modules or microservices if/when a particular domain (e.g., scheduling) requires independent scaling or release cadence
- Maintain OpenAPI-first approach to promote contract clarity and consumer confidence
