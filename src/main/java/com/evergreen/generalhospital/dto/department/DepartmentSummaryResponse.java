package com.evergreen.generalhospital.dto.department;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "DepartmentSummary", description = "Summary of department profile")
public record DepartmentSummaryResponse(
    @Schema(description = "Department identifier", example = "a0b1f54e-98c4-4e4d-9412-2eaf3e0c8695")
    UUID departmentId,

    @Schema(description = "Department name", example = "Cardiology")
    String name,

    @Schema(description = "Department description", example = "Handles heart and cardiovascular care")
    String description
) {
}
