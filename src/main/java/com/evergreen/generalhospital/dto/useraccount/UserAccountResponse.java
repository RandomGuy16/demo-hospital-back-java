package com.evergreen.generalhospital.dto.useraccount;

import com.evergreen.generalhospital.models.useraccount.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.List;
import java.util.UUID;

public record UserAccountResponse(
    @Schema(example = "deadbeef-0000-4000-8000-abcdefabcdef")
    UUID userAccountId,

    @Schema(example = "Doctor")
    String displayName,

    @Schema(example = "Strange")
    String username,

    @Schema(example = "keycloak")
    String provider,

    @Schema(example = "1234567890")
    String providerSubject,

    @Schema(example = "ROLE_ADMIN")
    @Enumerated(EnumType.STRING)
    List<Role> roles,

    @Schema(example = "bat.man@example.com")
    String email) {
}
