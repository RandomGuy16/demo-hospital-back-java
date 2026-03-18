package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record PractitionerRequest(
        @Schema(example = "Gregory")
        @NotBlank String firstName,

        @Schema(example = "House")
        @NotBlank String lastName,

        @Schema(example = "1234567890")
        @NotBlank @Size(min = 10, max = 10) String idNumber,

        @Schema(example = "1980-06-11")
        @NotNull @Past LocalDate dateOfBirth,

        @Schema(example = "male")
        @NotBlank String gender,

        @Schema(example = "+1 555 0200")
        @NotBlank @Size(min = 6, max = 20) String phoneNumber,

        @Schema(example = "house@example.com")
        @NotBlank String contacts,

        @Schema(example = "[\"Diagnostics\", \"Nephrology\"]")
        List<String> specialties) {
}
