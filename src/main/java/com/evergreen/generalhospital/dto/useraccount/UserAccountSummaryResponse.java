package com.evergreen.generalhospital.dto.useraccount;

import com.evergreen.generalhospital.models.useraccount.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;
import java.util.UUID;

@Schema(name = "UserAccountSummary", description = "Non-sensitive user account summary")
public record UserAccountSummaryResponse(
    @Schema(description = "User account identifier", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    UUID userAccountId,

    @Schema(description = "Display name", example = "Jane Doe")
    String displayName,

    @Schema(description = "Username", example = "jane.doe")
    String username,

    @Schema(description = "Email address", example = "jane.doe@example.com")
    String email,

    @Schema(description = "Assigned security roles", example = "[\"ROLE_PRACTITIONER\"]")
    Set<Role> roles
) {
}
