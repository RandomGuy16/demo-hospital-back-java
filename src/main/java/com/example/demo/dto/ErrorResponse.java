package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record ErrorResponse(
    @Schema(example = "VALIDATION_ERROR")
    String code,
    @Schema(example = "firstName must not be blank")
    String message,
    @Schema(example = "2026-03-17T12:45:00Z")
    Instant timestamp
) {
}
