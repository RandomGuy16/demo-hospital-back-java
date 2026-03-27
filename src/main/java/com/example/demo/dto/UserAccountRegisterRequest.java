package com.example.demo.dto;

import com.example.demo.models.useraccount.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserAccountRegisterRequest(
    @Schema(example = "Gregory House")
    @NotBlank String displayName,

    @Schema(example = "g.house")
    @NotBlank String username,

    @Schema(example = "alkjadshf-1234-5kjhl134-sf13f42df")
    java.util.UUID practitionerId,

    @Schema(example = "alkjadshf-1234-5kjhl134-sf13f42df")
    java.util.UUID patientId,

    @Schema(example = "ROLE_ADMIN")
    @NotNull Role role,

    @Schema(example = "bat.man@example.com")
    @Email
    @NotBlank String email,

    @Schema(example = "strong-password")
    @NotBlank String password
) {
}
