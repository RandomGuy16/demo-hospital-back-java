# Graph Report - api  (2026-08-21)

## Corpus Check
- 113 files · ~22,485 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 872 nodes · 2187 edges · 34 communities (28 shown, 6 thin omitted)
- Extraction: 83% EXTRACTED · 17% INFERRED · 0% AMBIGUOUS · INFERRED: 377 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `be8e4e73`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- PatientController.java
- AppointmentServiceTest
- Department
- AppointmentService.java
- PractitionerController.java
- CrudControllerTestSupport
- AuthControllerTest
- Spring Boot Application
- AppointmentController.java
- SecurityConfig.java
- UserAccount
- Person
- Appointment
- ValidAppointmentTime
- Patient
- PatientService.java
- Practitioner
- AppointmentRequest
- TestLog
- DemoController.java
- SwaggerConfig.java
- DemoApplicationTests.java
- gradlew
- Hardening Guidelines
- DemoApplication
- MedicalRecord
- DeletePatientRequest.java
- Departments Resource
- Phase 5 Compliance & Hardening
- AppointmentServiceTest.java
- PractitionerRequest
- AppointmentService

## God Nodes (most connected - your core abstractions)
1. `Patient` - 82 edges
2. `Practitioner` - 68 edges
3. `UserAccount` - 64 edges
4. `Appointment` - 63 edges
5. `Department` - 57 edges
6. `Person` - 37 edges
7. `CrudControllerTestSupport` - 36 edges
8. `PatientRepository` - 33 edges
9. `PatientService` - 30 edges
10. `UserAccountService` - 27 edges

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

## Communities (34 total, 6 thin omitted)

### Community 0 - "PatientController.java"
Cohesion: 0.08
Nodes (27): PatchMapping, Sort, ApiResponse, ApiResponses, Authentication, DeleteMapping, GetMapping, Jwt (+19 more)

### Community 1 - "AppointmentServiceTest"
Cohesion: 0.20
Nodes (4): Override, Override, AppointmentServiceTest, Test

### Community 2 - "Department"
Cohesion: 0.05
Nodes (33): DepartmentController, ApiResponse, ApiResponses, DeleteMapping, GetMapping, Operation, Page, PostMapping (+25 more)

### Community 3 - "AppointmentService.java"
Cohesion: 0.08
Nodes (24): AuthenticationException, ConstraintViolationException, EntityNotFoundException, ExceptionHandler, HttpStatus, MethodArgumentNotValidException, RestControllerAdvice, ApiException (+16 more)

### Community 4 - "PractitionerController.java"
Cohesion: 0.15
Nodes (17): ApiResponse, ApiResponses, DeleteMapping, GetMapping, Logger, Operation, Page, PostMapping (+9 more)

### Community 5 - "CrudControllerTestSupport"
Cohesion: 0.06
Nodes (34): AuthControllerTestSupport, AfterEach, AutoConfigureMockMvc, BeforeEach, ComponentScan, MockMvc, SpringBootTest, TestSubjects (+26 more)

### Community 6 - "AuthControllerTest"
Cohesion: 0.09
Nodes (21): AuthController, ApiResponse, Authentication, AuthenticationManager, GetMapping, Jwt, Operation, PostMapping (+13 more)

### Community 7 - "Spring Boot Application"
Cohesion: 0.07
Nodes (37): Controller → Service → Repository Layering, Conventional Commits, Java 21 Toolchain, Spring Boot Application, Spring Profiles (dev/test/prod), Bean Validation, Development Workflow, API App Service (+29 more)

### Community 8 - "AppointmentController.java"
Cohesion: 0.15
Nodes (16): AppointmentController, ApiResponse, ApiResponses, DeleteMapping, GetMapping, Operation, Page, PostMapping (+8 more)

### Community 9 - "SecurityConfig.java"
Cohesion: 0.11
Nodes (20): AuthenticationConfiguration, Claims, ConfigurationProperties, CorsConfigurationSource, DaoAuthenticationProvider, EnableWebSecurity, HttpSecurity, JwtAuthenticationConverter (+12 more)

### Community 10 - "UserAccount"
Cohesion: 0.05
Nodes (31): ApiResponse, ApiResponses, GetMapping, Operation, PostMapping, RequestMapping, ResponseEntity, RestController (+23 more)

### Community 11 - "Person"
Cohesion: 0.06
Nodes (9): Inheritance, Entity, PrePersist, PreUpdate, Schema, Table, Person, Repository (+1 more)

### Community 12 - "Appointment"
Cohesion: 0.11
Nodes (10): Appointment, Entity, PrePersist, PreUpdate, Table, AppointmentRepository, Override, Repository (+2 more)

### Community 13 - "ValidAppointmentTime"
Cohesion: 0.14
Nodes (15): ConstraintValidator, Constraint, Retention, Target, ValidAppointmentTime, ConstraintValidatorContext, Override, ValidAppointmentTimeValidator (+7 more)

### Community 14 - "Patient"
Cohesion: 0.05
Nodes (34): JpaRepository, PatientIdentityMismatchException, RepeatedUsernameException, UnclearUserRoleException, Entity, PrimaryKeyJoinColumn, Schema, Table (+26 more)

### Community 15 - "PatientService.java"
Cohesion: 0.13
Nodes (11): ImmutableFieldException, RepeatedIdNumberException, Page, Pageable, Service, Transactional, Page, Pageable (+3 more)

### Community 16 - "Practitioner"
Cohesion: 0.12
Nodes (6): PractitionerMapper, Entity, PrimaryKeyJoinColumn, Schema, Table, Practitioner

### Community 17 - "AppointmentRequest"
Cohesion: 0.25
Nodes (4): AppointmentRequest, GuestAppointmentRequest, AppointmentControllerTest, Test

### Community 18 - "TestLog"
Cohesion: 0.39
Nodes (3): MvcResult, Logger, TestLog

### Community 19 - "DemoController.java"
Cohesion: 0.36
Nodes (4): DemoController, GetMapping, RestController, DemoResponseBody

### Community 20 - "SwaggerConfig.java"
Cohesion: 0.53
Nodes (4): OpenAPI, Bean, Configuration, SwaggerConfig

### Community 21 - "DemoApplicationTests.java"
Cohesion: 0.60
Nodes (3): DemoApplicationTests, SpringBootTest, Test

### Community 22 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 23 - "Hardening Guidelines"
Cohesion: 0.50
Nodes (4): Phase 4 Reliability & Scale, Hardening Guidelines, Secret Management, Vulnerability Reporting

### Community 31 - "AppointmentServiceTest.java"
Cohesion: 0.17
Nodes (8): AppointmentResponse, Schema, AppointmentStatus, CANCELLED, COMPLETED, SCHEDULED, BeforeEach, ExtendWith

### Community 32 - "PractitionerRequest"
Cohesion: 0.30
Nodes (3): PractitionerRequest, Test, PractitionerControllerTest

## Knowledge Gaps
- **26 isolated node(s):** `DeletePatientRequest`, `NOT_FOUND`, `VALIDATION_ERROR`, `CONFLICT`, `RELATED_ENTITY_NOT_FOUND` (+21 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **6 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Patient` connect `Patient` to `PatientController.java`, `AppointmentServiceTest`, `Department`, `AppointmentService.java`, `AppointmentService`, `CrudControllerTestSupport`, `UserAccount`, `Person`, `Appointment`, `PatientService.java`, `AppointmentServiceTest.java`?**
  _High betweenness centrality (0.192) - this node is a cross-community bridge._
- **Why does `Practitioner` connect `Practitioner` to `PractitionerRequest`, `AppointmentServiceTest`, `Department`, `AppointmentService.java`, `PractitionerController.java`, `AppointmentService`, `CrudControllerTestSupport`, `UserAccount`, `Person`, `Appointment`, `Patient`, `PatientService.java`, `AppointmentServiceTest.java`?**
  _High betweenness centrality (0.182) - this node is a cross-community bridge._
- **Why does `UserAccount` connect `UserAccount` to `CrudControllerTestSupport`, `AuthControllerTest`, `SecurityConfig.java`, `ValidAppointmentTime`, `Patient`, `PatientService.java`, `Practitioner`?**
  _High betweenness centrality (0.182) - this node is a cross-community bridge._
- **What connects `DeletePatientRequest`, `NOT_FOUND`, `VALIDATION_ERROR` to the rest of the system?**
  _26 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `PatientController.java` be split into smaller, more focused modules?**
  _Cohesion score 0.07885304659498207 - nodes in this community are weakly interconnected._
- **Should `Department` be split into smaller, more focused modules?**
  _Cohesion score 0.05322947095098994 - nodes in this community are weakly interconnected._
- **Should `AppointmentService.java` be split into smaller, more focused modules?**
  _Cohesion score 0.08181818181818182 - nodes in this community are weakly interconnected._