package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PractitionerResponse(
    @Schema(example = "deadbeef-0000-4000-8000-abcdefabcdef")
    UUID practitionerId,

    @Schema(example = "Doctor")
    String firstName,

    @Schema(example = "Strange")
    String lastName,

    @Schema(example = "1970-02-29")
    LocalDate dateOfBirth,

    @Schema(example = "mystical-being")
    String gender,

    @Schema(example = "+1 404 404 0404")
    String phoneNumber,

    @Schema(example = "doctor.strange@kamar-taj.example.com")
    String contacts,

    @Schema(example = "['neurology', 'surgery']")
    List<String> specialties,

    @Schema(example = "['neurosurgery']")
    List<String> departments
) {
}
