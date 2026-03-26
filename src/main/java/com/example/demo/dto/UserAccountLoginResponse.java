package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserAccountLoginResponse(
    @Schema(example = "a;lskdhflkjhfo29uif1hiuvh03ubv424v78b")
    String token) {
}
