# Graph Report - .  (2026-08-19)

## Corpus Check
- Corpus is ~18,927 words - fits in a single context window. You may not need a graph.

## Summary
- 802 nodes · 1925 edges · 31 communities (26 shown, 5 thin omitted)
- Extraction: 85% EXTRACTED · 15% INFERRED · 0% AMBIGUOUS · INFERRED: 288 edges (avg confidence: 0.8)
- Token cost: 12,500 input · 4,500 output

## Community Hubs (Navigation)
- Patient API
- Appointment Domain
- Department API
- Error Handling
- Practitioner API
- Test Support & Fixtures
- Authentication & JWT
- Project Docs & Conventions
- Appointment Scheduling API
- Security Configuration
- User Account Entity & Mapper
- Person Domain Base
- User Account API
- Validation Constraints
- User Account Service & Repo
- Role Enum
- User Account Registration
- User Account Errors & Tests
- Test Logging Helper
- Demo Controller
- OpenAPI Config
- App Context Tests
- Gradle Wrapper
- Security Docs
- Application Entry Point
- Medical Record Model
- Delete Patient Request
- Departments OpenAPI Tag
- Compliance Roadmap

## God Nodes (most connected - your core abstractions)
1. `Patient` - 70 edges
2. `Practitioner` - 67 edges
3. `UserAccount` - 63 edges
4. `Appointment` - 62 edges
5. `Department` - 57 edges
6. `CrudControllerTestSupport` - 33 edges
7. `PatientRepository` - 31 edges
8. `Person` - 28 edges
9. `AppointmentRepository` - 26 edges
10. `UserAccountService` - 26 edges

## Surprising Connections (you probably didn't know these)
- `Layered Architecture (API/Application/Domain/Infrastructure)` --semantically_similar_to--> `Vertical Slice Packages`  [INFERRED] [semantically similar]
  docs/ARCHITECTURE.md → PROJECT_GUIDELINES.md
- `OAuth2/OIDC Authentication` --conceptually_related_to--> `Spring Boot Application`  [INFERRED]
  PROJECT_GUIDELINES.md → AGENTS.md
- `Modular Monolith` --references--> `Spring Boot Application`  [EXTRACTED]
  docs/ARCHITECTURE.md → AGENTS.md
- `Vertical Slice Packages` --rationale_for--> `Spring Boot Application`  [EXTRACTED]
  PROJECT_GUIDELINES.md → AGENTS.md
- `Spring Profiles (dev/test/prod)` --conceptually_related_to--> `API App Service`  [INFERRED]
  AGENTS.md → docker-compose.yml

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Phased Evolution Plan** — roadmap_phase0_foundations, roadmap_phase1_core_domain, roadmap_phase2_scheduling_access_control, roadmap_phase3_medical_records_integrations, roadmap_phase4_reliability_scale, roadmap_phase5_compliance_hardening [EXTRACTED 1.00]
- **Core Hospital Domain Model Resources** — docs_architecture_domain_model, docs_openapi_patient_schema, docs_openapi_practitioners_tag, docs_openapi_departments_tag, docs_openapi_appointments_tag, docs_openapi_medicalrecords_tag [INFERRED 0.85]
- **Local Development Environment** — docker_compose_postgres_service, docker_compose_app_service, docker_compose_hospitaldb, agents_spring_profiles [INFERRED 0.75]

## Communities (31 total, 5 thin omitted)

### Community 0 - "Patient API"
Cohesion: 0.05
Nodes (36): PatchMapping, ApiResponse, ApiResponses, DeleteMapping, GetMapping, Logger, Operation, Page (+28 more)

### Community 1 - "Appointment Domain"
Cohesion: 0.06
Nodes (21): AppointmentRequest, GuestAppointmentRequest, Appointment, Entity, PrePersist, PreUpdate, Schema, Table (+13 more)

### Community 2 - "Department API"
Cohesion: 0.06
Nodes (32): DepartmentController, ApiResponse, ApiResponses, DeleteMapping, GetMapping, Operation, Page, PostMapping (+24 more)

### Community 3 - "Error Handling"
Cohesion: 0.06
Nodes (36): AuthenticationException, ConstraintViolationException, EntityNotFoundException, ExceptionHandler, HttpStatus, JpaRepository, MethodArgumentNotValidException, RestControllerAdvice (+28 more)

### Community 4 - "Practitioner API"
Cohesion: 0.07
Nodes (30): ApiResponse, ApiResponses, DeleteMapping, GetMapping, Logger, Operation, Page, PostMapping (+22 more)

### Community 5 - "Test Support & Fixtures"
Cohesion: 0.07
Nodes (30): ObjectMapper, AuthControllerTestSupport, AutoConfigureMockMvc, BeforeEach, ComponentScan, MockMvc, SpringBootTest, TestSubjects (+22 more)

### Community 6 - "Authentication & JWT"
Cohesion: 0.11
Nodes (20): Authentication, Jwt, AuthController, ApiResponse, AuthenticationManager, GetMapping, Operation, PostMapping (+12 more)

### Community 7 - "Project Docs & Conventions"
Cohesion: 0.07
Nodes (37): Controller → Service → Repository Layering, Conventional Commits, Java 21 Toolchain, Spring Boot Application, Spring Profiles (dev/test/prod), Bean Validation, Development Workflow, API App Service (+29 more)

### Community 8 - "Appointment Scheduling API"
Cohesion: 0.12
Nodes (19): Sort, AppointmentController, ApiResponse, ApiResponses, DeleteMapping, GetMapping, Operation, Page (+11 more)

### Community 9 - "Security Configuration"
Cohesion: 0.13
Nodes (18): AuthenticationConfiguration, Claims, DaoAuthenticationProvider, EnableWebSecurity, HttpSecurity, JwtAuthenticationConverter, JwtDecoder, SecretKey (+10 more)

### Community 10 - "User Account Entity & Mapper"
Cohesion: 0.10
Nodes (6): Entity, PrePersist, PreUpdate, Schema, Table, UserAccount

### Community 11 - "Person Domain Base"
Cohesion: 0.08
Nodes (5): MappedSuperclass, PrePersist, PreUpdate, Schema, Person

### Community 12 - "User Account API"
Cohesion: 0.14
Nodes (14): ApiResponse, ApiResponses, GetMapping, Operation, PostMapping, RequestMapping, ResponseEntity, RestController (+6 more)

### Community 13 - "Validation Constraints"
Cohesion: 0.14
Nodes (15): ConstraintValidator, Constraint, Retention, Target, ValidAppointmentTime, ConstraintValidatorContext, Override, ValidAppointmentTimeValidator (+7 more)

### Community 14 - "User Account Service & Repo"
Cohesion: 0.17
Nodes (10): Repository, UserAccountRepository, Override, PasswordEncoder, Service, Transactional, UserDetails, UserAccountRefs (+2 more)

### Community 15 - "Role Enum"
Cohesion: 0.16
Nodes (11): Role, ROLE_ADMIN, ROLE_PATIENT, ROLE_PRACTITIONER, ROLE_RECEPTIONIST, PasswordEncoder, TestComponent, UserAccountFactory (+3 more)

### Community 16 - "User Account Registration"
Cohesion: 0.38
Nodes (3): UserAccountRequest, Test, UserAccountServiceTest

### Community 17 - "User Account Errors & Tests"
Cohesion: 0.20
Nodes (5): RepeatedUsernameException, UnclearUserRoleException, BeforeEach, ExtendWith, PasswordEncoder

### Community 18 - "Test Logging Helper"
Cohesion: 0.39
Nodes (3): MvcResult, Logger, TestLog

### Community 19 - "Demo Controller"
Cohesion: 0.36
Nodes (4): DemoController, GetMapping, RestController, DemoResponseBody

### Community 20 - "OpenAPI Config"
Cohesion: 0.53
Nodes (4): OpenAPI, Bean, Configuration, SwaggerConfig

### Community 21 - "App Context Tests"
Cohesion: 0.60
Nodes (3): DemoApplicationTests, SpringBootTest, Test

### Community 22 - "Gradle Wrapper"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 23 - "Security Docs"
Cohesion: 0.50
Nodes (4): Phase 4 Reliability & Scale, Hardening Guidelines, Secret Management, Vulnerability Reporting

## Knowledge Gaps
- **26 isolated node(s):** `DeletePatientRequest`, `NOT_FOUND`, `VALIDATION_ERROR`, `CONFLICT`, `RELATED_ENTITY_NOT_FOUND` (+21 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **5 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `UserAccount` connect `User Account Entity & Mapper` to `Patient API`, `Practitioner API`, `Test Support & Fixtures`, `Authentication & JWT`, `Security Configuration`, `User Account API`, `Validation Constraints`, `User Account Service & Repo`, `Role Enum`, `User Account Registration`, `User Account Errors & Tests`?**
  _High betweenness centrality (0.191) - this node is a cross-community bridge._
- **Why does `Patient` connect `Patient API` to `Appointment Domain`, `Department API`, `Error Handling`, `Test Support & Fixtures`, `User Account Entity & Mapper`, `Person Domain Base`, `User Account Service & Repo`, `Role Enum`, `User Account Registration`, `User Account Errors & Tests`?**
  _High betweenness centrality (0.189) - this node is a cross-community bridge._
- **Why does `Practitioner` connect `Practitioner API` to `Appointment Domain`, `Department API`, `Error Handling`, `Test Support & Fixtures`, `User Account Entity & Mapper`, `Person Domain Base`, `User Account Service & Repo`, `Role Enum`, `User Account Registration`, `User Account Errors & Tests`?**
  _High betweenness centrality (0.185) - this node is a cross-community bridge._
- **What connects `DeletePatientRequest`, `NOT_FOUND`, `VALIDATION_ERROR` to the rest of the system?**
  _26 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Patient API` be split into smaller, more focused modules?**
  _Cohesion score 0.0506872852233677 - nodes in this community are weakly interconnected._
- **Should `Appointment Domain` be split into smaller, more focused modules?**
  _Cohesion score 0.05660945498343872 - nodes in this community are weakly interconnected._
- **Should `Department API` be split into smaller, more focused modules?**
  _Cohesion score 0.055135135135135134 - nodes in this community are weakly interconnected._