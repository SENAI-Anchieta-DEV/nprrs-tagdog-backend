package com.senai.nprrs_tagdog_backend.application.dto;

import com.senai.nprrs_tagdog_backend.domain.entity.LocalCoordenadas;
import com.senai.nprrs_tagdog_backend.domain.entity.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record LocalCoordenadasDTO(
        @NotNull
        @NotBlank
        @Schema(description = "Cep do local")
        String cep,
        @NotNull
        @NotBlank
        @Schema(description = "Latitude do local")
        String latitude,
        @NotNull
        @NotBlank
        @Schema(description = "Longitude do local")
        String longitude,
        @NotNull
        @Schema(description = "Raio seguro")
        Integer raio
) {
        public LocalCoordenadas toEntity() {
                return LocalCoordenadas.builder()
                        .cep(this.cep)
                        .latitude(this.latitude)
                        .longitude(this.longitude)
                        .raio(this.raio)
                        .build();
        }

        public static LocalCoordenadasDTO fromEntity(LocalCoordenadas localCoordenadas) {
                return new LocalCoordenadasDTO(
                        localCoordenadas.getCep(),
                        localCoordenadas.getLatitude(),
                        localCoordenadas.getLongitude(),
                        localCoordenadas.getRaio()
                );
        }
}
