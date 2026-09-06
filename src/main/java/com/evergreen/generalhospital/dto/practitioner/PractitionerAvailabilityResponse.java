package com.evergreen.generalhospital.dto.practitioner;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "Practitioner availability schedule for a specific date")
public record PractitionerAvailabilityResponse(
    @Schema(example = "d2719c5d-84d1-43f6-a713-eef8a694be75")
    @NotNull UUID practitionerId,

    @Schema(example = "2026-09-01")
    @NotNull LocalDate date,

    List<PractitionerFreeTimeSlot> availableSlots
) {
}
