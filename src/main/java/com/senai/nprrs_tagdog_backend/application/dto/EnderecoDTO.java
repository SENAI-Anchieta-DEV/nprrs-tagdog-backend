package com.senai.nprrs_tagdog_backend.application.dto;

import com.senai.nprrs_tagdog_backend.domain.entity.Endereco;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EnderecoDTO {
    public record EnderecoRegistroDTO(
            @NotNull
            @NotBlank
            @Email
            @Schema(description = "Cep do tutor", example = "000000-00")
            String cep,
            @NotNull
            @NotBlank
            @Schema(description = "Numero do endereco", example = "00")
            String numero,
            @NotNull
            @NotBlank
            @Schema(description = "Complemento do endereco", example = "Apartamento")
            String complemento
    ) {}
    public record EnderecoResponseDTO(
            @Schema(description = "Cep do tutor")
            String cep,
            @Schema(description = "Numero do endereco")
            String numero,
            @Schema(description = "Complemento do endereco")
            String complemento
    ) {
        public static EnderecoResponseDTO fromEntity(Endereco endereco) {
            return new EnderecoResponseDTO(
                    endereco.getCep(),
                    endereco.getNumero(),
                    endereco.getComplemento()
            );
        }
    }
}
