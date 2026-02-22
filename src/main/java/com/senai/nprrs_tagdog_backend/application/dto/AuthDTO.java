package com.senai.nprrs_tagdog_backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthDTO {

    public record LoginRequest(
            @Schema(description = "Email do usuario")
            String email,
            @Schema(description = "Senha do usuario")
            String senha
    ) {}

    public record TokenResponse(
            @Schema(description = "Token do usuario")
            String token
    ) {}
}