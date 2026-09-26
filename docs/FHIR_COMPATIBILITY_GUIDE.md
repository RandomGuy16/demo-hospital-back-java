# Architectural Guide: HL7 FHIR Compatibility & Integration

**Project:** Evergreen General Hospital API  
**Target Standard:** HL7 FHIR Release 4 (R4) / R5  
**Audience:** Backend Engineering Team  
**Date:** September 2026  

---

## 1. Executive Summary

**Fast Healthcare Interoperability Resources (FHIR)** is the international standard defined by Health Level Seven (HL7) for exchanging clinical and administrative healthcare data over RESTful APIs.

This guide provides the full architectural context, mapping matrices, and step-by-step implementation options for adding FHIR compatibility to the **Evergreen General Hospital** backend whenever required by compliance mandates (e.g., ONC 21st Century Cures Act, CMS Interoperability rules) or third-party integrations (e.g., Apple Health, Epic/Cerner bridges, insurance claims).

---

## 2. Resource Mapping Matrix

The Evergreen General Hospital data model maps directly to core FHIR R4 resources:

| Evergreen Domain Entity | FHIR R4 Resource | FHIR JSON Resource Description | Key Field Mappings |
| :--- | :--- | :--- | :--- |
| [`Person`](file:///home/aroon/Projects/hospital/api/src/main/java/com/evergreen/generalhospital/models/Person.java) | **`Person`** | The physical human actor | `person_id` $\rightarrow$ `Person.id`<br>`idNumber` $\rightarrow$ `Person.identifier` (system: national ID)<br>`firstName`, `lastName` $\rightarrow$ `Person.name`<br>`phoneNumber` $\rightarrow$ `Person.telecom`<br>`dateOfBirth` $\rightarrow$ `Person.birthDate` |
| [`Patient`](file:///home/aroon/Projects/hospital/api/src/main/java/com/evergreen/generalhospital/models/patient/Patient.java) | **`Patient`** | An individual receiving care | `patient_id` $\rightarrow$ `Patient.id`<br>`mrn` $\rightarrow$ `Patient.identifier` (system: hospital-mrn)<br>`address` $\rightarrow$ `Patient.address`<br>`emergencyContact` $\rightarrow$ `Patient.contact` |
| [`Practitioner`](file:///home/aroon/Projects/hospital/api/src/main/java/com/evergreen/generalhospital/models/practitioner/Practitioner.java) | **`Practitioner`** | A healthcare professional | `practitioner_id` $\rightarrow$ `Practitioner.id`<br>`specialties` $\rightarrow$ `Practitioner.qualification.code` |
| [`Department`](file:///home/aroon/Projects/hospital/api/src/main/java/com/evergreen/generalhospital/models/department/Department.java) | **`Organization`** / **`Location`** | Hospital department or clinic | `department_id` $\rightarrow$ `Organization.id`<br>`name` $\rightarrow$ `Organization.name`<br>`description` $\rightarrow$ `Organization.alias` |
| `department_practitioners` | **`PractitionerRole`** | Practitioner duties at a specific department | Links `PractitionerRole.practitioner` to `Practitioner`<br>Links `PractitionerRole.organization` to `Organization`<br>`specialty` $\rightarrow$ `PractitionerRole.specialty` |
| [`Appointment`](file:///home/aroon/Projects/hospital/api/src/main/java/com/evergreen/generalhospital/models/appointment/Appointment.java) | **`Appointment`** | Scheduled clinical encounter | `appointment_id` $\rightarrow$ `Appointment.id`<br>`start` $\rightarrow$ `Appointment.start`<br>`end` $\rightarrow$ `Appointment.end`<br>`status` $\rightarrow$ `Appointment.status`<br>`chiefComplaint` $\rightarrow$ `Appointment.description`<br>`triageUrgency` $\rightarrow$ `Appointment.priority` |
| [`MedicalRecord`](file:///home/aroon/Projects/hospital/api/src/main/java/com/evergreen/generalhospital/models/medicalrecord/MedicalRecord.java) | **`Condition`** / **`Observation`** | Diagnoses, notes, vital signs | Medical history, notes, and lab results |

---

## 3. Recommended Architecture: The FHIR Facade Pattern

Rather than rewriting our clean, existing Spring Boot relational model, the industry standard pattern for existing hospital platforms is the **FHIR Facade (Adapter) Pattern**:

```
                       +-------------------------------+
                       |   Third-Party Consumers       |
                       |  (Apple Health, EHRs, Ins.)   |
                       +---------------+---------------+
                                       | (Standard FHIR JSON)
                                       v
                       +-------------------------------+
                       |         FHIR Gateway          |
                       |  (Routes: /fhir/r4/Patient..) |
                       +---------------+---------------+
                                       |
                   Translates FHIR <======> Internal DTOs
                                       |
                                       v
                       +-------------------------------+
                       |    Evergreen Core Services    |
                       | (PatientService, ApptService) |
                       +---------------+---------------+
                                       |
                                       v
                       +-------------------------------+
                       |      PostgreSQL Database      |
                       +-------------------------------+
```

### Advantages of the Facade:
1. **Zero Impact on Existing Frontend:** The Next.js frontend continues using lightweight `/api/v1/**` endpoints.
2. **Standard Interoperability:** External integrations query `/fhir/r4/**` and receive compliant FHIR bundles.
3. **Single Source of Truth:** Both APIs read from and write to the same underlying PostgreSQL tables.

---

## 4. Technology Stack: HAPI FHIR in Spring Boot

In the Java ecosystem, **[HAPI FHIR](https://hapifhir.io/)** (by University Health Network / Smile Digital Health) is the reference implementation:

### Gradle Dependencies
```kotlin
dependencies {
    // HAPI FHIR R4 Core Structures & Parser
    implementation("ca.uhn.hapi.fhir:hapi-fhir-base:7.4.0")
    implementation("ca.uhn.hapi.fhir:hapi-fhir-structures-r4:7.4.0")
    implementation("ca.uhn.hapi.fhir:hapi-fhir-server:7.4.0")
}
```

### Implementation: Resource Provider Example
In HAPI FHIR, endpoints are exposed using annotated **Resource Providers**:

```java
@Component
public class PatientResourceProvider implements IResourceProvider {

    private final PatientService patientService;

    public PatientResourceProvider(PatientService patientService) {
        this.patientService = patientService;
    }

    @Override
    public Class<Patient> getResourceType() {
        return Patient.class; // org.hl7.fhir.r4.model.Patient
    }

    /**
     * Handles: GET /fhir/r4/Patient/{id}
     */
    @Read
    public Patient readPatient(@IdParam IdType id) {
        UUID patientId = UUID.fromString(id.getIdPart());
        com.evergreen.generalhospital.models.patient.Patient domainPatient =
                patientService.getPatientById(patientId);

        Patient fhirPatient = new Patient();
        fhirPatient.setId(domainPatient.getPatientId().toString());

        // Identifiers (MRN and National ID)
        fhirPatient.addIdentifier()
                .setSystem("https://evergreen.hospital/mrn")
                .setValue(domainPatient.getMrn());

        fhirPatient.addIdentifier()
                .setSystem("https://evergreen.hospital/id-number")
                .setValue(domainPatient.getIdNumber());

        // Name
        fhirPatient.addName()
                .setFamily(domainPatient.getLastName())
                .addGiven(domainPatient.getFirstName());

        // Telecom (Phone)
        fhirPatient.addTelecom()
                .setSystem(ContactPoint.ContactPointSystem.PHONE)
                .setValue(domainPatient.getPhoneNumber());

        // Date of Birth & Gender
        fhirPatient.setBirthDate(java.sql.Date.valueOf(domainPatient.getDateOfBirth()));
        fhirPatient.setGender(mapGenderToFhir(domainPatient.getGender()));

        return fhirPatient;
    }
}
```

---

## 5. FHIR JSON Payload Examples

### 5.1 Patient Resource (`GET /fhir/r4/Patient/3fa85f64-5717-4562-b3fc-2c963f66afa6`)
```json
{
  "resourceType": "Patient",
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "identifier": [
    {
      "system": "https://evergreen.hospital/mrn",
      "value": "MRN-1234567890"
    },
    {
      "system": "https://evergreen.hospital/id-number",
      "value": "1234567890"
    }
  ],
  "active": true,
  "name": [
    {
      "use": "official",
      "family": "Doe",
      "given": ["John"]
    }
  ],
  "telecom": [
    {
      "system": "phone",
      "value": "+1 555 0100"
    }
  ],
  "gender": "male",
  "birthDate": "1995-04-18",
  "address": [
    {
      "line": ["123 Main St"]
    }
  ]
}
```

### 5.2 Appointment Resource (`GET /fhir/r4/Appointment/de305d54-75b4-431b-adb2-eb6b9e546014`)
```json
{
  "resourceType": "Appointment",
  "id": "de305d54-75b4-431b-adb2-eb6b9e546014",
  "status": "booked",
  "priority": 1,
  "description": "Severe chest pressure and shortness of breath",
  "start": "2026-04-10T09:00:00Z",
  "end": "2026-04-10T09:30:00Z",
  "participant": [
    {
      "actor": {
        "reference": "Patient/3fa85f64-5717-4562-b3fc-2c963f66afa6",
        "display": "John Doe"
      },
      "status": "accepted"
    },
    {
      "actor": {
        "reference": "Practitioner/d2719c5d-84d1-43f6-a713-eef8a694be75",
        "display": "Dr. Sarah Connor"
      },
      "status": "accepted"
    }
  ]
}
```

---

## 6. Clinical Vocabulary & Terminology Standards

When connecting to external health networks, free-text strings are mapped to standard healthcare coding systems:

| Medical Concept | Standard Vocabulary | Example Coding |
| :--- | :--- | :--- |
| **Chief Complaint / Symptoms** | **SNOMED-CT** | `29857009` (*Chest pain*) |
| **Triage Urgency Priority** | **FHIR v3 ActPriority** | `EM` (*Emergency*), `UR` (*Urgent*), `R` (*Routine*) |
| **Diagnoses & Conditions** | **ICD-10-CM** | `I20.0` (*Unstable angina*) |
| **Lab Tests & Observations** | **LOINC** | `8867-4` (*Heart rate*) |

---

## 7. Security: SMART on FHIR

**SMART on FHIR** is the standard OAuth2 / OpenID Connect profile for securing FHIR APIs:
- Access tokens contain clinical scopes:
  - `patient/Patient.read` (patient can read their own demographics)
  - `patient/Appointment.read` (patient can view their appointments)
  - `user/Appointment.write` (clinical staff or patient can schedule an appointment)
- Our existing Spring Security JWT configuration (`SecurityConfig.java`) can easily be extended to evaluate SMART scopes.

---

## 8. Implementation Checklist for Future FHIR Phase

When you are ready to activate FHIR compatibility:
- [ ] Add HAPI FHIR starter dependencies (`hapi-fhir-base`, `hapi-fhir-structures-r4`).
- [ ] Register `FhirServlet` or Spring controller mapping under `/fhir/r4/**`.
- [ ] Implement `PatientResourceProvider` backed by `PatientService`.
- [ ] Implement `PractitionerResourceProvider` backed by `PractitionerService`.
- [ ] Implement `AppointmentResourceProvider` backed by `AppointmentService`.
- [ ] Expose capability statement (`GET /fhir/r4/metadata`).
- [ ] Validate endpoints using the official [HL7 FHIR Validator](https://validator.fhir.org/).
