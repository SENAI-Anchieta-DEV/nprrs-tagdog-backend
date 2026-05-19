package com.senai.nprrs_tagdog_backend.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senai.nprrs_tagdog_backend.domain.entity.Tag;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TagDTO {

    public record TagRegistroDTO(
            @JsonProperty("mac") String numero,
            @JsonProperty("bateria_pct") Integer bateriaPorcentagem,
            @JsonProperty("sinal") Integer sinal,
            @JsonProperty("rede") String rede,
            @JsonProperty("modo") String modo,
            @JsonProperty("lat") String latitude,
            @JsonProperty("lon") String longitude,
            @JsonProperty("velocidade_kmh") Double velocidadeKmh,
            @JsonProperty("fuga") Boolean fuga,
            @JsonProperty("atividade") String atividade,
            @JsonProperty("status") String statusGps,
            @JsonProperty("satelites_vistos") Integer satelitesVistos,
            @JsonProperty("data_hora") String dataCriado
    ) {
        public Tag toEntity() {
            // O formato deve ser EXATAMENTE o que o Arduino envia: "%d/%m/%Y %H:%M:%S"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            return Tag.builder()
                    .numero(this.numero)
                    .latitude(this.latitude)
                    .longitude(this.longitude)
                    .dataCriado(LocalDateTime.parse(this.dataCriado, formatter))
                    .ativo(true)
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
            AnimalDTO.AnimalResponseSemTutorDTO animal,
            @Schema(description = "Data Criado")
            LocalDateTime dataCriado,
            @Schema(description = "Saida nao autorizada")
            boolean saidaNaoAutorizada,
            @Schema(description = "Ativo")
            boolean ativo
    ) {
        public static TagDTO.TagResponseDTO fromEntity(Tag tag) {
            AnimalDTO.AnimalResponseSemTutorDTO animal = AnimalDTO.AnimalResponseSemTutorDTO.fromEntity(tag.getAnimal());
            return new TagDTO.TagResponseDTO(
                    tag.getNumero(),
                    tag.getLatitude(),
                    tag.getLongitude(),
                    animal,
                    tag.getDataCriado(),
                    tag.isSaidaNaoAutorizada(),
                    tag.isAtivo()
            );
        }
    }
}