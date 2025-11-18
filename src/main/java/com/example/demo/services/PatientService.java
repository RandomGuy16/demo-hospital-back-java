package com.example.demo.services;

import com.example.demo.models.Patient;
import com.example.demo.repositories.PatientRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PatientService {
    private final PatientRepository patientRepository;

    // once i had a problem because LSP didnt find the "bean" of PatientRepository
    // it meant that spring didnt recognize it, that's why we use decorators
    /*
    @Component - Generic bean
    @Service - Business logic layer (like your services)
    @Repository - Data access layer (your repositories)
    @Controller / @RestController - Web layer (your API controllers)
    @Configuration - Configuration classes
    */

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public Optional<Patient> getPatientById(UUID id) {
        return patientRepository.findById(id);
    }

    public Optional<Patient> getPatientByMRN(String mrn) {
        return patientRepository.findByMrn(mrn);
    }

    public Optional<Patient> updatePatient(UUID id, Patient patient) {
        return patientRepository.findById(id)
            .map(p -> {
                p.setFirstName(patient.getFirstName());
                p.setLastName(patient.getLastName());
                p.setDateOfBirth(patient.getDateOfBirth());
                p.setGender(patient.getGender());
                p.setPhoneNumber(patient.getPhoneNumber());
                p.setContacts(patient.getContacts());
                return patientRepository.save(p);
            });
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<Patient> deletePatientById(UUID id) {
        return patientRepository.findById(id)
            .map(patient -> {
                patientRepository.delete(patient);
                return patient;
            });
    }
}
