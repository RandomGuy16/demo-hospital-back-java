package com.evergreen.generalhospital.mappers;

import com.evergreen.generalhospital.dto.admin.AdminPractitionerResponse;
import com.evergreen.generalhospital.dto.practitioner.PractitionerAvailabilityResponse;
import com.evergreen.generalhospital.dto.practitioner.PractitionerFreeTimeSlot;
import com.evergreen.generalhospital.dto.practitioner.PractitionerResponse;
import com.evergreen.generalhospital.dto.practitioner.PractitionerSummaryResponse;
import com.evergreen.generalhospital.models.department.Department;
import com.evergreen.generalhospital.models.practitioner.Practitioner;
import com.evergreen.generalhospital.models.useraccount.UserAccount;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class PractitionerMapper {
    // classical CRUD response, not to be used
    @Deprecated
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


    // Response for availability check on practitioners
    public static PractitionerAvailabilityResponse practitionerFreeTimeSlotsToResponse(
        UUID practitionerId,
        LocalDate date,
        List<PractitionerFreeTimeSlot> freeSlots) {
        return new PractitionerAvailabilityResponse(practitionerId, date, freeSlots);
    }


    // Admin detailed response
    public static AdminPractitionerResponse toAdminPractitionerResponse(
            Practitioner practitioner,
            UserAccount account) {
        return new AdminPractitionerResponse(
            practitioner.getPractitionerId(),
            practitioner.getFirstName(),
            practitioner.getLastName(),
            practitioner.getIdNumber(),
            practitioner.getDateOfBirth(),
            practitioner.getGender(),
            practitioner.getPhoneNumber(),
            practitioner.getEmergencyContact(),
            practitioner.getSpecialties() != null ? practitioner.getSpecialties() : List.of(),
            practitioner.getDepartments() != null
                ? practitioner.getDepartments().stream()
                    .map(DepartmentMapper::departmentToDepartmentSummaryResponse)
                    .toList()
                : List.of(),
            UserAccountMapper.userAccountToUserAccountSummaryResponse(account),
            practitioner.getCreatedAt()
        );
    }

    // Brief response
    public static PractitionerSummaryResponse toPractitionerSummaryResponse(Practitioner practitioner) {
        return new PractitionerSummaryResponse(
            practitioner.getPractitionerId(),
            practitioner.getFirstName(),
            practitioner.getLastName(),
            practitioner.getIdNumber(),
            practitioner.getGender(),
            practitioner.getPhoneNumber(),
            practitioner.getSpecialties() != null ? practitioner.getSpecialties() : List.of(),
            practitioner.getDepartments() != null
                ? practitioner.getDepartments().stream()
                    .map(DepartmentMapper::departmentToDepartmentSummaryResponse)
                    .toList()
                : List.of()
        );
    }
}
