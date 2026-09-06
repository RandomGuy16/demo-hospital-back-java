package com.evergreen.generalhospital.dto.practitioner;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PractitionerFreeTimeSlot(
    @Schema(example = "2026-09-01T09:00:00")
    @NotNull LocalDateTime start,

    @Schema(example = "2026-09-01T09:30:00")
    @NotNull LocalDateTime end
) {
}
