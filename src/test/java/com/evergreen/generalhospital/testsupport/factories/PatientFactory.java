package com.evergreen.generalhospital.testsupport.factories;

import com.evergreen.generalhospital.models.patient.Patient;
import com.evergreen.generalhospital.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import java.time.LocalDate;

@TestComponent
public class PatientFactory {

    @Autowired
    private PatientRepository patientRepository;

    public Patient savePatient(String firstName, String lastName, String idNumber) {
        return savePatient(
            firstName,
            lastName,
            idNumber,
            "female",
            LocalDate.of(1992, 1, 10)
        );
    }

    public Patient savePatient(String firstName,
                                  String lastName,
                                  String idNumber,
                                  String gender,
                                  LocalDate dateOfBirth) {

        String normalizedGender = (gender == null || gender.isBlank())
            ? "female"
            : gender;

        LocalDate normalizedDob = (dateOfBirth != null)
            ? dateOfBirth
            : LocalDate.of(1992, 1, 10);

        Patient patient = new Patient(
            firstName,
            lastName,
            idNumber,
            normalizedDob,
            normalizedGender,
            "+1 555 0100",
            firstName.toLowerCase() + "@example.com",
            "MRN-" + idNumber,
            "123 Main St"
        );

        return patientRepository.save(patient);
    }
}
