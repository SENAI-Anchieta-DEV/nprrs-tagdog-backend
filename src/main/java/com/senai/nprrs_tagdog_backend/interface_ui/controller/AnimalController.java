package com.senai.nprrs_tagdog_backend.interface_ui.controller;

import com.senai.nprrs_tagdog_backend.application.dto.AnimalDTO;
import com.senai.nprrs_tagdog_backend.application.service.AnimalService;
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

@Tag(name = "Animal", description = "Gerenciamento de animais")
@RestController
@RequestMapping("/api/animais")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService service;

    @Operation(
            summary = "Cadastrar animal",
            description = "Realiza o cadastro de um animal"
    )
    @ApiResponse(responseCode = "201", description = "Animal cadastrado com sucesso")
    @PostMapping
    public ResponseEntity<AnimalDTO.AnimalResponseDTO> criar(
            @Valid @RequestBody AnimalDTO.AnimalRegistroDTO dto) {

        AnimalDTO.AnimalResponseDTO animal = service.registrar(dto);

        return ResponseEntity.created(
                URI.create("/api/animais/matricula/" + animal.matricula())
        ).body(animal);
    }

    @Operation(
            summary = "Listar animais",
            description = "Retorna todos os animais cadastrados"
    )
    @GetMapping
    public ResponseEntity<List<AnimalDTO.AnimalResponseDTO>> listar() {

        return ResponseEntity.ok(service.listar());
    }

    @Operation(
            summary = "Buscar animal por matrícula",
            description = "Retorna um animal a partir da matrícula"
    )
    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<AnimalDTO.AnimalResponseDTO> buscarPorMatricula(
            @Parameter(description = "Matrícula do animal", example = "C12")
            @PathVariable String matricula) {

        return ResponseEntity.ok(service.buscarPorMatricula(matricula));
    }

    @Operation(
            summary = "Atualizar animal",
            description = "Atualiza os dados de um animal pela matrícula"
    )
    @PutMapping("/matricula/{matricula}")
    public ResponseEntity<AnimalDTO.AnimalResponseDTO> atualizar(
            @PathVariable String matricula,
            @Valid @RequestBody AnimalDTO.AnimalRegistroDTO dto) {

        return ResponseEntity.ok(service.atualizar(matricula, dto));
    }

    @Operation(
            summary = "Desativar animal",
            description = "Realiza o soft delete do animal"
    )

    @DeleteMapping("/deletar/matricula/{matricula}")
    public void deletar(@PathVariable String matricula) {
        service.deletar(matricula);
    }
}