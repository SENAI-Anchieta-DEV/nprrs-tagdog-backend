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
            @Schema(description = "Cep do tutor", example = "000000-00")
            String cep,
            @NotNull
            @NotBlank
            @Schema(description = "Rua", example = "Rua Gandavo")
            String rua,
            @NotNull
            @NotBlank
            @Schema(description = "Bairro", example = "Vila Mariana")
            String bairro,
            @NotNull
            @NotBlank
            @Schema(description = "Cidade", example = "São Paulo")
            String cidade,
            @NotNull
            @NotBlank
            @Schema(description = "Estado", example = "São Paulo")
            String estado,
            @NotNull
            @NotBlank
            @Schema(description = "Numero do endereco", example = "00")
            String numero,
            @NotNull
            @NotBlank
            @Schema(description = "Complemento do endereco", example = "Apartamento")
            String complemento
    ) {
        public Endereco toEntity() {
            return Endereco.builder()
                    .cep(this.cep)
                    .rua(this.rua)
                    .bairro(this.bairro)
                    .cidade(this.cidade)
                    .estado(this.estado)
                    .numero(this.numero)
                    .complemento(this.complemento)
                    .build();
        }
    }
    public record EnderecoResponseDTO(
            @Schema(description = "Cep do tutor")
            String cep,
            @Schema(description = "Rua")
            String rua,
            @Schema(description = "Bairro")
            String bairro,
            @Schema(description = "Cidade")
            String cidade,
            @Schema(description = "Estado")
            String estado,
            @Schema(description = "Numero do endereco")
            String numero,
            @Schema(description = "Complemento do endereco")
            String complemento
    ) {
        public static EnderecoResponseDTO fromEntity(Endereco endereco) {
            return new EnderecoResponseDTO(
                    endereco.getCep(),
                    endereco.getRua(),
                    endereco.getBairro(),
                    endereco.getCidade(),
                    endereco.getEstado(),
                    endereco.getNumero(),
                    endereco.getComplemento()
            );
        }
    }
}
