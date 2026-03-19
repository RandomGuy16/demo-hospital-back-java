package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientPatchRequest(
    @Schema(example = "Jane")
    @NotBlank String firstName,

    @Schema(example = "Doe")
    @NotBlank String lastName,

    @Schema(example = "1234567890")
    @NotBlank @Size(min = 10, max = 10) String idNumber,

    @Schema(example = "1995-04-18")
    @Past(message = "dateOfBirth must be in the past") LocalDate dateOfBirth,

    @Schema(example = "female")
    @NotBlank String gender,

    @Schema(example = "+1 555 0100")
    @NotBlank @Size(min = 6, max = 20) String phoneNumber,

    @Schema(example = "jane.doe@example.com")
    @NotBlank String contacts,

    @Schema(example = "123 Main St, Springfield")
    @NotBlank String address) {
};
