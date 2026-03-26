package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserAccountLoginRequest(
    @Schema(example = "example@gmail.com")
    String email,
    @Schema(example = "password (never use a simple password :) )")
    String password) {
}
