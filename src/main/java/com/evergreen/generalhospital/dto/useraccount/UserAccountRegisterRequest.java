package com.evergreen.generalhospital.dto.useraccount;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserAccountRegisterRequest(
    @Schema(example = "Jane")
    @NotBlank @Size(max = 50) String firstName,

    @Schema(example = "Doe")
    @NotBlank @Size(max = 50) String lastName,

    @Schema(example = "1234567890")
    @NotBlank @Pattern(regexp = "\\d{10}", message = "idNumber must be exactly 10 digits") String idNumber,

    @Schema(example = "1995-04-18")
    @NotNull @Past(message = "dateOfBirth must be in the past") LocalDate dateOfBirth,

    @Schema(example = "female")
    @NotBlank @Size(max = 20) String gender,

    @Schema(example = "+1 555 0100")
    @NotBlank @Size(min = 6, max = 20) String phoneNumber,

    @Schema(example = "jane.doe@example.com")
    @Size(max = 200) String emergencyContact,

    @Schema(example = "123 Main St, Springfield")
    @NotBlank @Size(max = 200) String address,

    @Schema(example = "jane.doe@example.com")
    @Email @NotBlank String email,

    @Schema(example = "strong-password")
    @NotBlank @Size(min = 6, max = 100) String password
) {
}