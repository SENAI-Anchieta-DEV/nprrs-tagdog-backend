package com.senai.nprrs_tagdog_backend.application.dto;

import com.senai.nprrs_tagdog_backend.domain.entity.*;
import com.senai.nprrs_tagdog_backend.domain.repository.AnimalRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AnimalDTO {
    public record AnimalRegistroDTO(
            @Schema(description = "Imagem do animal")
            String imagem,

            @NotBlank
            @Schema(description = "Nome do animal", example = "Bob")
            String nome,
            
            @NotBlank
            @Schema(description = "Raça do animal", example = "Golden Retriever")
            String raca,
            
            @NotNull
            @Schema(description = "Sexo do animal", example = "MACHO")
            SexoAnimal sexo,
            
            @NotNull
            @Schema(description = "Porte do animal", example = "GRANDE")
            PorteAnimal porte,
            
            @NotNull
            @Schema(description = "Data de nascimento do animal", example = "2026-02-19")
            LocalDate dataNascimento,
            
            @NotBlank
            @Schema(description = "Descrição do animal", example = "Alergia a chocolate")
            String descricao
    ) {
        public Animal toEntity() {
            return Animal.builder()
                    .imagem(this.imagem)
                    .nome(this.nome)
                    .raca(this.raca)
                    .sexo(this.sexo)
                    .porte(this.porte)
                    .dataNascimento(this.dataNascimento)
                    .descricao(this.descricao)
                    .ativo(true)
                    .build();
        }
    }

    public record AnimalResponseDTO(
            @Schema(description = "Imagem do animal")
            String imagem,

            @Schema(description = "Matricula do animal")
            String matricula,

            @Schema(description = "Tutor do animal")
            TutorDTO.TutorResponseDadosPrincipaisDTO tutor,

            @Schema(description = "Nome do animal")
            String nome,

            @Schema(description = "Raça do animal")
            String raca,

            @Schema(description = "Sexo do animal")
            SexoAnimal sexo,

            @Schema(description = "Porte do animal")
            PorteAnimal porte,

            @Schema(description = "Data de nascimento do animal")
            LocalDate dataNascimento,

            @Schema(description = "Descrição do animal")
            String descricao,

            @Schema(description = "Numero da tag do animal")
            String numeroTag,

            @Schema(description = "Animal ativo ou não")
            boolean ativo
    ) {
        public static AnimalResponseDTO fromEntity(Animal animal, Tutor tutor) {
            return new AnimalResponseDTO(
                    animal.getImagem(),
                    animal.getMatricula(),
                    TutorDTO.TutorResponseDadosPrincipaisDTO.fromEntity(tutor),
                    animal.getNome(),
                    animal.getRaca(),
                    animal.getSexo(),
                    animal.getPorte(),
                    animal.getDataNascimento(),
                    animal.getDescricao(),
                    animal.getNumeroTag(),
                    animal.isAtivo()
            );
        }
    }

    public record AnimalResponseSemTutorDTO(
            @Schema(description = "Imagem do animal")
            String imagem,

            @Schema(description = "Matricula do animal")
            String matricula,

            @Schema(description = "Nome do animal")
            String nome,

            @Schema(description = "Raça do animal")
            String raca,

            @Schema(description = "Sexo do animal")
            SexoAnimal sexo,

            @Schema(description = "Porte do animal")
            PorteAnimal porte,

            @Schema(description = "Data de nascimento do animal")
            LocalDate dataNascimento,

            @Schema(description = "Descrição do animal")
            String descricao,

            @Schema(description = "Numero da tag do animal")
            String numeroTag,

            @Schema(description = "Animal ativo ou não")
            boolean ativo
    ) {
        public static AnimalResponseSemTutorDTO fromEntity(Animal animal) {
            return new AnimalResponseSemTutorDTO(
                    animal.getImagem(),
                    animal.getMatricula(),
                    animal.getNome(),
                    animal.getRaca(),
                    animal.getSexo(),
                    animal.getPorte(),
                    animal.getDataNascimento(),
                    animal.getDescricao(),
                    animal.getNumeroTag(),
                    animal.isAtivo()
            );
        }
    }
}