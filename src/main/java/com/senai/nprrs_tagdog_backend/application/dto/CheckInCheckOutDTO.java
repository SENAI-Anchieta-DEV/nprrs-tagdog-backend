package com.senai.nprrs_tagdog_backend.application.dto;

import com.senai.nprrs_tagdog_backend.domain.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CheckInCheckOutDTO(
        @NotNull
        @Schema(description = "Check-in ou checkout")
        CheckInOuCheckOut checkInOuCheckOut,

        @NotNull
        @Schema(description = "Data e hora do check-in/checkout")
        LocalDateTime dataHora
) {
    public CheckInCheckOut toEntity() {
        return CheckInCheckOut.builder()
                .checkInOuCheckOut(this.checkInOuCheckOut)
                .dataHora(this.dataHora)
                .build();
    }
}