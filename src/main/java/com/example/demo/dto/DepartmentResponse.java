package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DepartmentResponse(
        @Schema(example = "a0b1f54e-98c4-4e4d-9412-2eaf3e0c8695")
        UUID departmentId,

        @Schema(example = "Cardiology")
        String name,

        @Schema(example = "Handles heart and cardiovascular care")
        String description,

        @Schema(example = "[\"Dr. Gregory House\", \"Dr. Meredith Grey\"]")
        List<String> practitioners,

        @Schema(example = "2026-03-17T12:30:00")
        LocalDateTime createdAt,

        @Schema(example = "2026-03-17T12:45:00")
        LocalDateTime updatedAt
) {
}
