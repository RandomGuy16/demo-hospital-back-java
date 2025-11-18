package com.example.demo.controllers;

import com.example.demo.models.Patient;
import com.example.demo.services.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {
    private final PatientService patientService;

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // now we are going to make some CRUD ops

    // CREATE - POST /api/v1/patients
    // they send us a patient object
    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        Patient created = patientService.createPatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ ALL - get all patients /api/v1/patients
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        logger.info("GET /api/v1/patients Request");
        List<Patient> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    // READ ONE - get patient by id /api/v1/patients/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable UUID id) {
        return patientService.getPatientById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // READ BY MRN - GET /api/v1/patients/mrn/{mrn}
    @GetMapping("/mrn/{mrn}")
    public ResponseEntity<Patient> getPatientByMrn(@PathVariable String mrn) {
        return patientService.getPatientByMRN(mrn)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE - PUT /api/v1/patients/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable UUID id, @RequestBody Patient patient) {
        return patientService.updatePatient(id, patient)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // DELETE - DELETE /api/v1/patients/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Patient> deletePatient(@PathVariable UUID id) {
        return patientService.deletePatientById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
