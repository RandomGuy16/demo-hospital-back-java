package com.evergreen.generalhospital.dto.practitioner;

import com.evergreen.generalhospital.dto.department.DepartmentSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(name = "PractitionerSummaryResponse", description = "Brief response for listing practitioners in the hospital admin page")
public record PractitionerSummaryResponse(
    @Schema(description = "Unique identifier of the practitioner", example = "d2719c5d-84d1-43f6-a713-eef8a694be75")
    UUID practitionerId,

    @Schema(description = "First name", example = "Jane")
    String firstName,

    @Schema(description = "Last name", example = "Doe")
    String lastName,

    @Schema(description = "National 10-digit identification number", example = "1234567890")
    String idNumber,

    @Schema(description = "Gender", example = "female")
    String gender,

    @Schema(description = "Contact phone number", example = "+1 555 0100")
    String phoneNumber,

    @Schema(description = "Medical specialties practiced by the practitioner", example = "[\"Cardiology\", \"Internal Medicine\"]")
    List<String> specialties,

    @Schema(description = "Hospital departments affiliated with the practitioner")
    List<DepartmentSummaryResponse> departments
) {}
