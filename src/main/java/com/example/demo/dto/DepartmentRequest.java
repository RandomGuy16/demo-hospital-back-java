package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record DepartmentRequest(
        @Schema(example = "Cardiology")
        @NotBlank String name,

        @Schema(example = "Handles heart and cardiovascular care")
        @NotBlank String description) {
}
