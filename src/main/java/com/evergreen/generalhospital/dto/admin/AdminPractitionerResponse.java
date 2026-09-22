package com.evergreen.generalhospital.dto.admin;

import com.evergreen.generalhospital.dto.department.DepartmentSummaryResponse;
import com.evergreen.generalhospital.dto.useraccount.UserAccountSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(name = "AdminPractitionerResponse", description = "Detailed response for newly onboarded practitioner and provisioned user account")
public record AdminPractitionerResponse(
    @Schema(description = "Unique identifier of the practitioner", example = "d2719c5d-84d1-43f6-a713-eef8a694be75")
    UUID practitionerId,

    @Schema(description = "First name", example = "Jane")
    String firstName,

    @Schema(description = "Last name", example = "Doe")
    String lastName,

    @Schema(description = "National 10-digit identification number", example = "1234567890")
    String idNumber,

    @Schema(description = "Date of birth", example = "1980-05-15")
    LocalDate dateOfBirth,

    @Schema(description = "Gender", example = "female")
    String gender,

    @Schema(description = "Contact phone number", example = "+1 555 0100")
    String phoneNumber,

    @Schema(description = "Emergency contact details (phone or email)", example = "emergency.contact@example.com")
    String emergencyContact,

    @Schema(description = "Medical specialties practiced by the practitioner", example = "[\"Cardiology\", \"Internal Medicine\"]")
    List<String> specialties,

    @Schema(description = "Hospital departments affiliated with the practitioner")
    List<DepartmentSummaryResponse> departments,

    @Schema(description = "Provisioned login account details")
    UserAccountSummaryResponse account,

    @Schema(description = "Creation timestamp", example = "2026-09-22T16:00:00")
    LocalDateTime createdAt
) {}
