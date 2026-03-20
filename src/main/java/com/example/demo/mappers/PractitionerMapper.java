package com.example.demo.mappers;

import com.example.demo.dto.PractitionerResponse;
import com.example.demo.models.Practitioner;

public class PractitionerMapper {

    public static PractitionerResponse practitionerToPractitionerResponse(Practitioner practitioner) {
        return new PractitionerResponse(
            practitioner.getPractitionerId(),
            practitioner.getFirstName(),
            practitioner.getLastName(),
            practitioner.getDateOfBirth(),
            practitioner.getGender(),
            practitioner.getPhoneNumber(),
            practitioner.getContacts(),
            practitioner.getSpecialties(),
            practitioner.getDepartments().stream().map(department -> department.getName()).toList()
        );
    }
}
