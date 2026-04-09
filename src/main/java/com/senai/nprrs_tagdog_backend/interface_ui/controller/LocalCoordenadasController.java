package com.senai.nprrs_tagdog_backend.interface_ui.controller;

import com.senai.nprrs_tagdog_backend.application.dto.AdminDTO;
import com.senai.nprrs_tagdog_backend.application.dto.LocalCoordenadasDTO;
import com.senai.nprrs_tagdog_backend.application.service.LocalCoordenadasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "LocalCoordenadas", description = "Gerenciamento de coordenadas de locais")
@RestController
@RequestMapping("/api/local")
@RequiredArgsConstructor
public class LocalCoordenadasController {

    private final LocalCoordenadasService service;

    @Operation(
            summary = "Cadastrar coordenada de local",
            description = "Realiza o cadastro de um local",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LocalCoordenadasDTO.class),
                            examples = @ExampleObject(name = "Exemplo válido", value = """
                                        {
                                              "cep": "04023-001",
                                              "latitude": "-23.591348043485937",
                                              "longitude": "-46.64516553880177",
                                              "raio": 10
                                          }
                                    """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cadastro realizado com sucesso"),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Entidade duplicada",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Coordenadas do local existe", value = "\"Coordenadas do local existe\""),
                                    }
                            )
                    ),
            }
    )
    @PostMapping()
    public ResponseEntity<LocalCoordenadasDTO> criar(@Valid @RequestBody LocalCoordenadasDTO dto) {
        LocalCoordenadasDTO local = service.registrar(dto);
        return ResponseEntity.created(
                URI.create("/api/local/" + local.cep())
        ).body(local);
    }

    @Operation(
            summary = "Buscar coordenada do local",
            description = "Retorna uma coordenada",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Administrador retornado com sucesso")
            }
    )
    @GetMapping()
    public ResponseEntity<LocalCoordenadasDTO> buscar() {
        return ResponseEntity.ok(service.buscar());
    }

    @Operation(
            summary = "Deletar coordenada",
            description = "Realiza o delete do local",
            parameters = {
                    @Parameter(name = "cep", description = "cep do local a ser desativado", example = "04023-001")
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Local removido com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Local não encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Local não encontrado", value = "\"Local não encontrado\""),
                                    }
                            )
                    )
            }
    )
    @DeleteMapping("/cep/{cep}")
    public void deletar(@PathVariable String cep) {
        service.deletar(cep);
    }
}