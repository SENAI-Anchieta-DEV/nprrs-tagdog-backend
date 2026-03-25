package com.senai.nprrs_tagdog_backend.application.dto;

import com.senai.nprrs_tagdog_backend.domain.entity.Animal;
import com.senai.nprrs_tagdog_backend.domain.entity.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TagDTO {
    public record TagRegistroDTO(
            @NotNull
            @NotBlank
            @Schema(description = "Numero da tag", example = "1")
            String numero,
            @NotNull
            @NotBlank
            @Schema(description = "Latitude", example = "-23.591316433799992")
            String latitude,
            @NotNull
            @NotBlank
            @Schema(description = "Longitude", example = "-46.64509172099156")
            String longitude,
            @NotNull
            @Schema(description = "Data Criado", example = "2026-03-24T10:00:00")
            String dataCriado
    ) {
        public Tag toEntity() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            return Tag.builder()
                    .numero(this.numero)
                    .latitude(this.latitude)
                    .longitude(this.longitude)
                    .dataCriado(LocalDateTime.parse(this.dataCriado, formatter))
                    .build();
        }
    }
    public record TagResponseDTO(
            @Schema(description = "Numero da tag")
            String numero,
            @Schema(description = "Latitude")
            String latitude,
            @Schema(description = "Longitude")
            String longitude,
            @Schema(description = "Animal que esta com a tag no momento")
            Animal animal,
            @Schema(description = "Data Criado")
            LocalDateTime dataCriado,
            @Schema(description = "Ativo")
            boolean ativo
    ) {
        public static TagDTO.TagResponseDTO fromEntity(Tag tag) {
            return new TagDTO.TagResponseDTO(
                    tag.getNumero(),
                    tag.getLatitude(),
                    tag.getLongitude(),
                    tag.getAnimal(),
                    tag.getDataCriado(),
                    tag.isAtivo()
            );
        }
    }
}
