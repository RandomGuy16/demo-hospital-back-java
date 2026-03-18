package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentRequest(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotNull UUID patientId,

        @Schema(example = "d2719c5d-84d1-43f6-a713-eef8a694be75")
        @NotNull UUID practitionerId,

        @Schema(example = "a0b1f54e-98c4-4e4d-9412-2eaf3e0c8695")
        @NotNull UUID departmentId,

        @Schema(example = "2026-04-10T09:00:00")
        @NotNull @Future LocalDateTime start,

        @Schema(example = "2026-04-10T09:30:00")
        @NotNull @Future LocalDateTime end,

        @Schema(example = "SCHEDULED")
        @NotBlank String status) {
}
