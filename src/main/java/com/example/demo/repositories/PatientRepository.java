package com.example.demo.repositories;

import com.example.demo.models.Patient;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

// The repos are all interfaces, so we can use them as mocks
// and they always inherit from JpaRepository to avoid the boilerplate DB code

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    // Spring automatically provides these methods:
    // - save(Patient patient)
    // - findById(UUID id)
    // - findAll()
    // - deleteById(UUID id)
    // - count()
    // and many more!

    Patient save(Patient patient);
    Optional<Patient> findById(UUID id);
    Optional<Patient> findByMrn(String mrn);

    List<Patient> findAll();
    List<Patient> findByFirstNameAndLastName(@NonNull String firstName, @NonNull String lastName);
    List<Patient> findByLastName(@NonNull String lastName);
    boolean existsByMrn(@NonNull String mrn);
}
