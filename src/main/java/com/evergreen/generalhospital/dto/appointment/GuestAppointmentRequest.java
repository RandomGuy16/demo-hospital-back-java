package com.evergreen.generalhospital.dto.appointment;

import com.evergreen.generalhospital.models.appointment.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record GuestAppointmentRequest(
        // identification data since the user is not registered
        @Schema(example = "9785164321")
        @NotNull String idNumber,

        @Schema(example = "+100 123455")
        @NotNull String phoneNumber,

        @Schema(example = ";alskdjf")
        @NotNull String email,

        @Schema(example = "fake street")
        @NotNull String location,
        

        // participants data
        @Schema(example = "blablabla")
        @NotNull String chiefComplaint,

        @Schema(example = "d2719c5d-84d1-43f6-a713-eef8a694be75")
        @NotNull UUID practitionerId,

        @Schema(example = "a0b1f54e-98c4-4e4d-9412-2eaf3e0c8695")
        @NotNull UUID departmentId,


        // metadata
        @Schema(example = "2026-04-10T09:00:00")
        @NotNull @Future LocalDateTime start,

        @Schema(example = "2026-04-10T09:30:00")
        @NotNull @Future LocalDateTime end,

        @Schema(example = "SCHEDULED")
        @NotNull AppointmentStatus status
) {}
