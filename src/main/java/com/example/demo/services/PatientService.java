package com.example.demo.services;

import com.example.demo.dto.CreatePatientRequest;
import com.example.demo.models.Patient;
import com.example.demo.repositories.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PatientService {
    private final PatientRepository patientRepository;

    // once I had a problem because LSP didn't find the "bean" of PatientRepository,
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

    private String generateMrn(String idNumber) {
        String random = UUID.randomUUID().toString().replace(" ", "").substring(0, 8).toUpperCase();
        return "ShokoIeiri-" + idNumber + random;
    }

    public Patient createPatient(CreatePatientRequest patient) {
        Patient newPatient = new Patient(
            patient.firstName(),
            patient.lastName(),
            patient.idNumber(),
            patient.dateOfBirth(),
            patient.gender(),
            patient.phoneNumber(),
            patient.contacts(),
            generateMrn(patient.idNumber()),
            patient.address()
        );
        return patientRepository.save(newPatient);
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

    // In order to implement pagination, at least at a basic level
    // we need to use the Pageable interface from springboot
    // we return a Page<Patient> instead of a List<Patient>
    // this is because the pageable interface returns a page of objects
    // and we pass a Pageable object to the repository method
    // to get the page of objects
    // theres no need to add something to the repository, since it inherits from JpaRepository

    public Page<Patient> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable);
    }

    public Optional<Patient> deletePatientById(UUID id) {
        return patientRepository.findById(id)
            .map(patient -> {
                patientRepository.delete(patient);
                return patient;
            });
    }
}
