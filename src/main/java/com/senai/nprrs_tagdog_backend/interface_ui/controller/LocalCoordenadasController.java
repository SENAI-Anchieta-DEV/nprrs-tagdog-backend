package com.senai.nprrs_tagdog_backend.interface_ui.controller;

import com.senai.nprrs_tagdog_backend.application.dto.LocalCoordenadasDTO;
import com.senai.nprrs_tagdog_backend.application.service.LocalCoordenadasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/local/")
@RequiredArgsConstructor
public class LocalCoordenadasController {

    private final LocalCoordenadasService service;

    @Operation(
            summary = "Cadastrar coordenada de local",
            description = "Realiza o cadastro de um local"
    )
    @ApiResponse(responseCode = "201", description = "Coordenadas cadastradas com sucesso")
    @PostMapping()
    public ResponseEntity<LocalCoordenadasDTO> criar(@Valid @RequestBody LocalCoordenadasDTO dto) {
        LocalCoordenadasDTO local = service.registrar(dto);
        return ResponseEntity.created(
                URI.create("/api/local/" + local.cep())
        ).body(local);
    }

    @Operation(
            summary = "Buscar coordenada do local",
            description = "Retorna uma coordenada"
    )
    @GetMapping()
    public ResponseEntity<LocalCoordenadasDTO> buscar() {
        return ResponseEntity.ok(service.buscar());
    }

    @Operation(
            summary = "Deletar coordenada",
            description = "Realiza o delete do local"
    )

    @DeleteMapping("/cep/{cep}")
    public void deletar(@PathVariable String cep) {
        service.deletar(cep);
    }
}