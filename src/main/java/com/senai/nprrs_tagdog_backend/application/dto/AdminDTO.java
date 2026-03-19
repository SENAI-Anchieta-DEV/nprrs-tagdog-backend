package com.senai.nprrs_tagdog_backend.application.dto;

import com.senai.nprrs_tagdog_backend.domain.entity.Admin;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AdminDTO {
    public record AdminRegistroDTO(
            @NotNull
            @NotBlank
            @Schema(description ="Nome do administrador", example="CarlosSousa Paula")
            String nome,
            @NotNull
            @NotBlank
            @Email
            @Schema(description = "Email do administrador", example="administrador@email.com")
            String email,
            @NotNull
            @NotBlank
            @Schema(description = "Senha do administrador", example="1234")
            String senha
    ){
        public Admin toEntity(){
            return Admin.builder()
                    .nome(this.nome)
                    .email(this.email)
                    .senha(this.senha)
                    .ativo(true)
                    .role(Role.ADMIN)
                    .build();
        }

    }
    public record AdminResponseDTO(
            @Schema(description = "Nome do administrador")
            String nome,
            @Schema(description = "Email do administrador")
            String email,
            @Schema(description = "Administrador ativo ou não")
            boolean ativo,
            @Schema(description = "Role")
            Role role
    ) {
        public static AdminResponseDTO fromEntity(Admin admin) {
            return new AdminResponseDTO(
                    admin.getNome(),
                    admin.getEmail(),
                    admin.isAtivo(),
                    admin.getRole()
            );
        }
    }
}
