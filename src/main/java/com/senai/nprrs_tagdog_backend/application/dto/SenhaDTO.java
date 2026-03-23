package com.senai.nprrs_tagdog_backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SenhaDTO (
        @NotNull
        @NotBlank
        @Schema(description = "Email do usuario")
        String email,
        @NotNull
        @Schema(description = "Token do usuario")
        Integer token,
        @NotNull
        @NotBlank
        @Schema(description = "Senha a ser alterada")
        String senha
) {}
