package com.evergreen.generalhospital.mappers;

import com.evergreen.generalhospital.dto.practitioner.PractitionerAvailabilityResponse;
import com.evergreen.generalhospital.dto.practitioner.PractitionerFreeTimeSlot;
import com.evergreen.generalhospital.dto.practitioner.PractitionerResponse;
import com.evergreen.generalhospital.models.practitioner.Practitioner;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

    public static PractitionerAvailabilityResponse practitionerFreeTimeSlotsToResponse(
        UUID practitionerId,
        LocalDate date,
        List<PractitionerFreeTimeSlot> freeSlots) {
        return new PractitionerAvailabilityResponse(practitionerId, date, freeSlots);
    }
}
