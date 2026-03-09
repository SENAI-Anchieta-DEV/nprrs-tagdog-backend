package com.senai.nprrs_tagdog_backend.application.dto;

import com.senai.nprrs_tagdog_backend.domain.entity.EmailToken;
import com.senai.nprrs_tagdog_backend.domain.entity.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EmailTokenDTO (
        @NotNull
        @Schema(description = "Usuario")
        Usuario usuario,
        @NotNull
        @Schema(description = "Token do usuario")
        Integer token,
        @NotNull
        @Schema(description = "Data de expiracao do token")
        LocalDateTime dataExpirado,
        @NotNull
        @Schema(description = "Data de criacao do token")
        LocalDateTime dataCriado
) {
    public static EmailTokenDTO fromEntity(EmailToken emailToken) {
        return new EmailTokenDTO(
                emailToken.getUsuario(),
                emailToken.getToken(),
                emailToken.getDataExpirado(),
                emailToken.getDataCriado()
        );
    }
}
