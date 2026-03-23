package com.senai.nprrs_tagdog_backend.application.dto;

import com.senai.nprrs_tagdog_backend.domain.entity.Funcionario;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FuncionarioDTO {
    public record FuncionarioRegistroDTO(
            @NotNull
            @NotBlank
            @Schema(description = "Nome do funcionario", example = "Bob Sobrenome")
            String nome,
            @NotNull
            @NotBlank
            @Email
            @Schema(description = "Email do funcionario", example = "funcionario@email.com")
            String email,
            @NotNull
            @NotBlank
            @Schema(description = "Senha do funcionario", example = "123")
            String senha
    ) {
        public Funcionario toEntity() {
            return Funcionario.builder()
                    .nome(this.nome)
                    .email(this.email)
                    .senha(this.senha)
                    .ativo(true)
                    .role(Role.FUNCIONARIO)
                    .build();
        }
    }
    public record FuncionarioResponseDTO(
            @Schema(description = "Nome do funcionario")
            String nome,
            @Schema(description = "Email do funcionario")
            String email,
            @Schema(description = "Animais no cuidado do funcionario")
            List<AnimalDTO.AnimalResponseSemTutorDTO> animais,
            @Schema(description = "Senha do funcionario")
            boolean ativo,
            @Schema(description = "Role")
            Role role
    ) {
        public static FuncionarioResponseDTO fromEntity(Funcionario funcionario) {
            List<AnimalDTO.AnimalResponseSemTutorDTO> animais = Optional.ofNullable(funcionario.getAnimais())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(AnimalDTO.AnimalResponseSemTutorDTO::fromEntity)
                    .toList();
            return new FuncionarioResponseDTO(
                    funcionario.getNome(),
                    funcionario.getEmail(),
                    animais,
                    funcionario.isAtivo(),
                    funcionario.getRole()
            );
        }
    }
}
