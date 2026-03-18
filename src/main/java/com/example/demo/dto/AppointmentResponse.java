package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponse(
        @Schema(example = "de305d54-75b4-431b-adb2-eb6b9e546014")
        UUID appointmentId,

        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID patientId,

        @Schema(example = "d2719c5d-84d1-43f6-a713-eef8a694be75")
        UUID practitionerId,

        @Schema(example = "a0b1f54e-98c4-4e4d-9412-2eaf3e0c8695")
        UUID departmentId,

        @Schema(example = "2026-04-10T09:00:00")
        LocalDateTime start,

        @Schema(example = "2026-04-10T09:30:00")
        LocalDateTime end,

        @Schema(example = "SCHEDULED")
        String status
) {
}
