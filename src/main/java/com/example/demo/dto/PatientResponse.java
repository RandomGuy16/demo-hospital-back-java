package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.UUID;

public record PatientResponse (
    @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    UUID patientId,
    @Schema(example = "Jane")
    String firstName,
    @Schema(example = "Doe")
    String lastName,
    @Schema(example = "1995-04-18")
    LocalDate dateOfBirth,
    @Schema(example = "female")
    String gender,
    @Schema(example = "+1 555 0100")
    String phoneNumber,
    @Schema(example = "jane.doe@example.com")
    String contacts,
    @Schema(example = "ShokoIeiri-1234567890ABCDEF12")
    String mrn,
    @Schema(example = "123 Main St, Springfield")
    String address
) {}
