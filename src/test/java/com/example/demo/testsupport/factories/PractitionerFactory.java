package com.example.demo.testsupport.factories;

import com.example.demo.models.practitioner.Practitioner;
import com.example.demo.repositories.PractitionerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import java.time.LocalDate;
import java.util.List;

@TestComponent
public class PractitionerFactory {

    @Autowired
    private PractitionerRepository practitionerRepository;

    public Practitioner savePractitioner(String firstName,
                                            String lastName,
                                            String idNumber,
                                            List<String> specialties) {
        return savePractitioner(
            firstName,
            lastName,
            idNumber,
            "female",
            LocalDate.of(1992, 1, 10),
            specialties
        );
    }

    public Practitioner savePractitioner(String firstName,
                                            String lastName,
                                            String idNumber,
                                            String gender,
                                            LocalDate dateOfBirth,
                                            List<String> specialties) {

        String normalizedGender = (gender == null || gender.isBlank())
            ? "female"
            : gender;

        LocalDate normalizedDob = (dateOfBirth != null)
            ? dateOfBirth
            : LocalDate.of(1992, 1, 10);

        Practitioner practitioner = new Practitioner(
            firstName,
            lastName,
            idNumber,
            normalizedDob,
            normalizedGender,
            "+1 555 0200",
            firstName.toLowerCase() + "@example.com"
        );
        practitioner.setSpecialties(specialties);
        return practitionerRepository.save(practitioner);
    }

}
