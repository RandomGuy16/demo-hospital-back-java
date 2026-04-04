package com.evergreen.generalhospital.mappers;

import com.evergreen.generalhospital.dto.PractitionerResponse;
import com.evergreen.generalhospital.models.practitioner.Practitioner;

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
