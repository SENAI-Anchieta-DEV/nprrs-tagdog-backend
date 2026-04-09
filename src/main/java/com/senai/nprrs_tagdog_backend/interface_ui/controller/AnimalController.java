package com.senai.nprrs_tagdog_backend.interface_ui.controller;

import com.senai.nprrs_tagdog_backend.application.dto.AnimalDTO;
import com.senai.nprrs_tagdog_backend.application.service.AnimalService;
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

@Tag(name = "Animal", description = "Gerenciamento de animais")
@RestController
@RequestMapping("/api/animais")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService service;

    @Operation(
            summary = "Cadastrar um novo animal",
            description = "Realiza o cadastro do animal",
            parameters = {
                    @Parameter(name = "emailOuCpfTutor", description = "email ou cpf do tutor a ter mais um animal", example = "admin@email.com")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AnimalDTO.AnimalRegistroDTO.class),
                            examples = @ExampleObject(name = "Exemplo válido", value = """
                                        {
                                              "imagem": "",
                                              "nome": "Bob",
                                              "raca": "Golden Retriever",
                                              "sexo": "MACHO",
                                              "porte": "GRANDE",
                                              "dataNascimento": "2026-02-19",
                                              "descricao": "Alergia a chocolate"
                                          }
                                    """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cadastro realizado com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Entidade não encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Tutor não encontrado", value = "\"Tutor não encontrado\""),
                                    }
                            )
                    ),
            }
    )
    @PostMapping("/emailOuCpfTutor/{emailOuCpfTutor}")
    public ResponseEntity<AnimalDTO.AnimalResponseDTO> criar(
            @Valid @RequestBody AnimalDTO.AnimalRegistroDTO dto, @PathVariable String emailOuCpfTutor) {

        AnimalDTO.AnimalResponseDTO animal = service.registrar(dto, emailOuCpfTutor);

        return ResponseEntity.created(
                URI.create("/api/animais/matricula/" + animal.matricula())
        ).body(animal);
    }

    @Operation(
            summary = "Listar animais",
            description = "Retorna todos os animais cadastrados",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
            }
    )
    @GetMapping
    public ResponseEntity<List<AnimalDTO.AnimalResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @Operation(
            summary = "Listar animais sem funcionario",
            description = "Retorna todos os animais sem funcionario cadastrados"
    )
    @GetMapping("/animalSemFuncionario")
    public ResponseEntity<List<AnimalDTO.AnimalResponseDTO>> listarAnimaisSemFuncionario() {
        return ResponseEntity.ok(service.listarAnimaisSemFuncionario());
    }

    @Operation(
            summary = "Buscar um animal por matrícula",
            description = "Retorna um animal a partir da matrícula",
            parameters = {
                    @Parameter(name = "matricula", description = "matricula do animal a ser buscado", example = "TD-12345")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Animal retornado com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Animal não encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "Animal não encontrado", value = "\"Animal não encontrado\"")
                            )
                    )
            }
    )
    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<AnimalDTO.AnimalResponseDTO> buscarPorMatricula(@PathVariable String matricula) {

        return ResponseEntity.ok(service.buscarPorMatricula(matricula));
    }

    @Operation(
            summary = "Atualizar um animal pela matricula",
            description = "Atualiza os dados de um animal pela matrícula",
            parameters = {
                    @Parameter(name = "matricula", description = "matricula do animal a ser atualizado", example = "TD-12345")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AnimalDTO.AnimalRegistroDTO.class),
                            examples = @ExampleObject(name = "Exemplo válido", value = """
                                        {
                                              "imagem": "",
                                              "matricula": "C12",
                                              "nome": "Bob2",
                                              "raca": "Golden Retriever",
                                              "sexo": "MACHO",
                                              "porte": "GRANDE",
                                              "dataNascimento": "2026-02-19",
                                              "descricao": "Alergia a chocolate"
                                          }
                                    """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Atualização realizada com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Animal não encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Animal não encontrado", value = "\"Animal não encontrado\""),
                                    }
                            )
                    )
            }
    )
    @PutMapping("/matricula/{matricula}")
    public ResponseEntity<AnimalDTO.AnimalResponseDTO> atualizar(
            @PathVariable String matricula,
            @Valid @RequestBody AnimalDTO.AnimalRegistroDTO dto) {

        return ResponseEntity.ok(service.atualizar(matricula, dto));
    }

    @Operation(
            summary = "Adicionar ou atualizar tag do animal",
            description = "Adicionar ou atualizar tag do animal pela matrícula",
            parameters = {
                    @Parameter(name = "matricula", description = "matricula do animal a ser atualizado", example = "TD-12345")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Atualização realizada com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Animal não encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Animal não encontrado", value = "\"Animal não encontrado\""),
                                    }
                            )
                    )
            }
    )
    @PutMapping("/matricula/{matricula}/tag/{tag}")
    public ResponseEntity<AnimalDTO.AnimalResponseDTO> atualizar(
            @PathVariable String matricula,
            @PathVariable String tag) {

        return ResponseEntity.ok(service.tag(matricula, tag));
    }

    @Operation(
            summary = "Desativar animal",
            description = "Realiza o soft delete do animal",
            parameters = {
                    @Parameter(name = "matricula", description = "matricula do animal a ser desativado", example = "TD-12345")
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Animal removido com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Animal não encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Animal não encontrado", value = "\"Animal não encontrado\""),
                                    }
                            )
                    )
            }
    )
    @DeleteMapping("/deletar/matricula/{matricula}")
    public void deletar(@PathVariable String matricula) {
        service.deletar(matricula);
    }
}