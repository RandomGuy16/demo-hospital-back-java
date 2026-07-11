package com.evergreen.generalhospital.dto.useraccount;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "CurrentUser", description = "Authenticated user details extracted from the JWT")
public record CurrentUserResponse(
        @Schema(example = "00u123example") String subject,
        @Schema(example = "https://accounts.google.com") String issuer,
        @Schema(example = "jane.doe@example.com") String email,
        @Schema(example = "Jane Doe") String name,
        @Schema(example = "jane.doe") String preferredUsername,
        @Schema(example = "[\"SCOPE_openid\", \"ROLE_ADMIN\"]") List<String> authorities
) {
}
