package com.example.demo.controllers;

import com.example.demo.dto.CreatePatientRequest;
import com.example.demo.dto.PatientResponse;
import com.example.demo.models.Patient;
import com.example.demo.services.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.net.URI;
import java.util.UUID;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {
    private final PatientService patientService;

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    private PatientResponse patientToPatientResponse(Patient patient) {
        /*if (patient == null) {
            return null;
        }*/
        return new PatientResponse(
            patient.getPatientId(),
            patient.getFirstName(),
            patient.getLastName(),
            patient.getDateOfBirth(),
            patient.getGender(),
            patient.getPhoneNumber(),
            patient.getContacts(),
            patient.getMrn(),
            patient.getAddress()
        );
    }

    // now we are going to make some CRUD ops

    // CREATE - POST /api/v1/patients
    // they send us a patient object
    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@RequestBody CreatePatientRequest req, UriComponentsBuilder uriBuilder) {
        // the service receives the dto request and returns the patient object
        Patient created = patientService.createPatient(req);
        PatientResponse response = patientToPatientResponse(created);

        URI location = uriBuilder.path("/api/v1/patients/{id}").buildAndExpand(created.getPatientId()).toUri();

        return ResponseEntity.created(location).body(response);
    }

    // READ ALL - get all patients /api/v1/patients
    @GetMapping
    public ResponseEntity<Page<PatientResponse>> getAllPatients(Pageable pageable) {
        logger.info("GET /api/v1/patients Request");
        Page<PatientResponse> patients = patientService.getAllPatients(pageable)
            .map(this::patientToPatientResponse);
        return ResponseEntity.ok(patients);
    }

    // READ ONE - get patient by id /api/v1/patients/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable UUID id) {
        Optional<Patient> patient = patientService.getPatientById(id);
        return patient
            .map(value -> ResponseEntity.ok(patientToPatientResponse(value)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // READ BY MRN - GET /api/v1/patients/mrn/{mrn}
    @GetMapping("/mrn/{mrn}")
    public ResponseEntity<PatientResponse> getPatientByMrn(@PathVariable String mrn) {
        Optional<Patient> patient = patientService.getPatientByMRN(mrn);
        return patient
            .map(value -> ResponseEntity.ok(patientToPatientResponse(value)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // UPDATE - PUT /api/v1/patients/{id}
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(@PathVariable UUID id, @RequestBody CreatePatientRequest req) {
        Optional<Patient> existingPatient = patientService.getPatientById(id);
        return existingPatient
            .map(value -> ResponseEntity.ok(patientToPatientResponse(value)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE - DELETE /api/v1/patients/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<PatientResponse> deletePatient(@PathVariable UUID id) {
        return patientService.deletePatientById(id)
            .map(value -> ResponseEntity.ok(patientToPatientResponse(value)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
