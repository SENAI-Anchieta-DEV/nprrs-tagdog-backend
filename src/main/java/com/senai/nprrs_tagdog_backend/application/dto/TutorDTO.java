package com.senai.nprrs_tagdog_backend.application.dto;

import com.senai.nprrs_tagdog_backend.domain.entity.Animal;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import com.senai.nprrs_tagdog_backend.domain.entity.Tutor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
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
            @Schema(description = "Endereco do tutor")
            EnderecoDTO.EnderecoRegistroDTO endereco,
            @NotNull
            @Schema(description = "Animail do tutor")
            AnimalDTO.AnimalRegistroDTO animal
    ) {
        public Tutor toEntity() {
            return Tutor.builder()
                    .nome(this.nome)
                    .email(this.email)
                    .senha(this.senha)
                    .cpf(this.cpf)
                    .telefone(this.telefone)
                    .endereco(this.endereco.toEntity())
                    .animais(new ArrayList<Animal>())
                    .ativo(true)
                    .role(Role.TUTOR)
                    .build();
        }
    }
    public record TutorAtualizacaoDTO(
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
            @Schema(description = "Cpf do tutor", example = "111.111.111-11")
            String cpf,
            @NotNull
            @NotBlank
            @Schema(description = "Telefone do tutor", example = "(11) 11111-1111")
            String telefone,
            @NotNull
            @Schema(description = "Endereco do tutor")
            EnderecoDTO.EnderecoRegistroDTO endereco
    ) {}
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
                    .map(animal -> AnimalDTO.AnimalResponseDTO.fromEntity(animal, tutor))
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
    public record TutorResponseDadosPrincipaisDTO(
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
            String telefone
    ) {
        public static TutorResponseDadosPrincipaisDTO fromEntity(Tutor tutor) {
            return new TutorResponseDadosPrincipaisDTO(
                    tutor.getNome(),
                    tutor.getEmail(),
                    tutor.isAtivo(),
                    tutor.getRole(),
                    tutor.getCpf(),
                    tutor.getTelefone()
            );
        }
    }
}
