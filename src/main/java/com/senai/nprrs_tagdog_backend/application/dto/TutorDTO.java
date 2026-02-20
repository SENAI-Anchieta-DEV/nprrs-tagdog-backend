package com.senai.nprrs_tagdog_backend.application.dto;

import com.senai.nprrs_tagdog_backend.domain.entity.Endereco;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import com.senai.nprrs_tagdog_backend.domain.entity.Tutor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class TutorDTO {
    public record TutorRegistroDTO(
            @NotNull
            @NotBlank
            @Schema(description = "Nome do tutor", example = "Nome Sobrenome")
            String nome,
            @NotNull
            @NotBlank
            @Email
            @Schema(description = "Email do tutor", example = "funcionario@email.com")
            String email,
            @NotNull
            @NotBlank
            @Schema(description = "Senha do tutor", example = "123")
            String senha,
            @NotNull
            @NotBlank
            @Schema(description = "Cpf do tutor", example = "111.111.111-11")
            String cpf,
            @NotNull
            @NotBlank
            @Schema(description = "Telefone do tutor", example = "(11) 11111-1111")
            String telefone,
            @NotNull
            @NotBlank
            @Schema(description = "Endereco do tutor")
            EnderecoDTO.EnderecoRegistroDTO endereco,
            @NotNull
            @NotBlank
            @Schema(description = "Lista de animais do tutor")
            List<AnimalDTO.AnimalRegistroDTO> animais
    ) {
        public Tutor toEntity() {
            return Tutor.builder()
                    .nome(this.nome)
                    .email(this.email)
                    .senha(this.senha)
                    .cpf(this.cpf)
                    .telefone(this.telefone)
                    .endereco(this.endereco.toEntity())
                    .animais(this.animais.stream()
                            .map(AnimalDTO.AnimalRegistroDTO::toEntity)
                            .toList())
                    .ativo(true)
                    .role(Role.FUNCIONARIO)
                    .build();
        }
    }
    public record TutorResponseDTO(
            @Schema(description = "Nome do tutor")
            String nome,
            @Schema(description = "Email do tutor")
            String email,
            @Schema(description = "Usuario ativo ou nao")
            boolean ativo,
            @Schema(description = "Role")
            Role role,
            @Schema(description = "Cpf do tutor")
            String cpf,
            @Schema(description = "Telefone do tutor")
            String telefone,
            @NotNull
            @NotBlank
            @Schema(description = "Endereco do tutor")
            EnderecoDTO.EnderecoResponseDTO endereco,
            @Schema(description = "Lista de animais do tutor")
            List<AnimalDTO.AnimalResponseDTO> animais
    ) {
        public static TutorResponseDTO fromEntity(Tutor tutor) {
            List<AnimalDTO.AnimalResponseDTO> animais = tutor.getAnimais().stream()
                    .map(AnimalDTO.AnimalResponseDTO::fromEntity)
                    .toList();
            EnderecoDTO.EnderecoResponseDTO endereco = EnderecoDTO.EnderecoResponseDTO.fromEntity(tutor.getEndereco());
            return new TutorResponseDTO(
                    tutor.getNome(),
                    tutor.getEmail(),
                    tutor.isAtivo(),
                    tutor.getRole(),
                    tutor.getCpf(),
                    tutor.getTelefone(),
                    endereco,
                    animais
            );
        }
    }
}
