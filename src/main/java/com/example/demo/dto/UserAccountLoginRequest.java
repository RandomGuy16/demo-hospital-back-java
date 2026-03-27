package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserAccountLoginRequest(
    @Schema(example = "example@gmail.com")
    @Email
    @NotBlank String email,
    @Schema(example = "password (never use a simple password :) )")
    @NotBlank String password) {
}
