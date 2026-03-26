package com.example.demo.dto;

import com.example.demo.models.useraccount.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserAccountRequest(
    @Schema(example = "Gregory House")
    @NotBlank String displayName,

    @Schema(example = "g.house")
    @NotBlank String username,

    @Schema(example = "alkjadshf-1234-5kjhl134-sf13f42df")
    UUID practitionerId,

    @Schema(example = "alkjadshf-1234-5kjhl134-sf13f42df")
    UUID patientId,

    @Schema(example = "keycloak")
    @NotBlank String provider,

    @Schema(example = "1234567890")
    @NotBlank String providerSubject,

    @Schema(example = "ROLE_ADMIN")
    @Enumerated(EnumType.STRING)
    @NotNull Role role,

    @Schema(example = "bat.man@example.com")
    @Email
    @NotBlank String email

) {}
