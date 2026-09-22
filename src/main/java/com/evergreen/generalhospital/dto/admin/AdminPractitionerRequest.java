package com.evergreen.generalhospital.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record AdminPractitionerRequest(
    @Schema(example = "Jane")
    @NotBlank String firstName,

    @Schema(example = "Doe")
    @NotBlank String lastName,

    @Schema(example = "1234567890")
    @NotBlank @Size(min = 10, max = 10) String idNumber,

    @Schema(example = "1980-05-15")
    @NotNull @Past LocalDate dateOfBirth,

    @Schema(example = "female")
    @NotBlank String gender,
    @Schema(example = "+1 555 0100")
    @NotBlank @Size(min = 6, max = 20) String phoneNumber,

    @Schema(example = "john.doe@example.com")
    @NotBlank String emergencyContact,

    @NotEmpty List<UUID> departmentIds,

    List<String> specialties,

    @Schema(example = "jane.doe@example.com")
    @NotBlank @Email String email,

    @Schema(example = "jane.doe")
    String username,

    @Schema(example = "strong-password-123")
    @NotBlank @Size(min = 6) String password
) {}
