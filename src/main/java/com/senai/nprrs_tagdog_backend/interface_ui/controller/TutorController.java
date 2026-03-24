package com.senai.nprrs_tagdog_backend.interface_ui.controller;

import com.senai.nprrs_tagdog_backend.application.dto.TutorDTO;
import com.senai.nprrs_tagdog_backend.application.service.TutorService;
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

@Tag(name = "Tutor", description = "Gerenciamento de tutores")
@RestController
@RequestMapping("/api/tutores")
@RequiredArgsConstructor
public class TutorController {
    private final TutorService tutorService;

    @Operation(
            summary = "Cadastrar um novo tutor",
            description = "Realiza o cadastro do tutor",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TutorDTO.TutorRegistroDTO.class),
                            examples = @ExampleObject(name = "Exemplo válido", value = """
                                        {
                                             "nome": "Nome",
                                             "email": "tutor@email.com",
                                             "senha": "senha",
                                             "cpf": "111.111.111-11",
                                             "telefone": "(11) 11111-1111",
                                             "endereco": {
                                                "cep": "111111-11",
                                                "rua": "Rua Gandavo",
                                                "bairro": "Vila Mariana",
                                                "cidade": "São Paulo",
                                                "estado": "São Paulo",
                                                "numero": "11",
                                                "complemento": "Apartamento"
                                             },
                                             "animal": {
                                                 "nome": "Bob",
                                                 "raca": "Golden Retriever",
                                                 "sexo": "MACHO",
                                                 "porte": "GRANDE",
                                                 "dataNascimento": "2026-02-19",
                                                 "descricao": "Alergia a chocolate"
                                               }
                                         }
                                    """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cadastro realizado com sucesso")
            }
    )
    @PostMapping
    public ResponseEntity<TutorDTO.TutorResponseDTO> registrarTutor(@Valid @RequestBody TutorDTO.TutorRegistroDTO dto) {
        TutorDTO.TutorResponseDTO novoTutor = tutorService.registrarTutor(dto);
        return ResponseEntity.created(
                URI.create("/api/tutores/email/" + novoTutor.email())
        ).body(novoTutor);
    }

    @Operation(
            summary = "Listar todos os tutores ativos",
            description = "Retorna todos os tutores cadastrados",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
            }
    )
    @GetMapping
    public ResponseEntity<List<TutorDTO.TutorResponseDTO>> listarFuncinariosAtivos() {
        return ResponseEntity.ok(tutorService.listarTutoresAtivos());
    }

    @Operation(
            summary = "Buscar um funcionario pelo email ou cpf",
            description = "Retorna um funcionario cadastrado",
            parameters = {
                    @Parameter(name = "emailOuCpf", description = "email ou cpf do funcionario a ser buscado", example = "tutor@email.com")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Funcionario retornado com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Funcionario não encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "Tutor não encontrado", value = "\"Tutor com email tutor@email.com não encontrado.\"")
                            )
                    )
            }
    )
    @GetMapping("/emailOuCpf/{emailOuCpf}")
    public ResponseEntity<TutorDTO.TutorResponseDTO> buscarTutorPorEmailOuCpf(@PathVariable String emailOuCpf) {
        return ResponseEntity.ok(tutorService.buscarTutorPorEmailOuCpf(emailOuCpf));
    }

    @Operation(
            summary = "Atualizar um tutor pelo email ou cpf",
            description = "Realiza a atualização do tutor",
            parameters = {
                    @Parameter(name = "emailOuCpf", description = "email ou cpf do tutor a ser atualizado", example = "tutor@email.com")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TutorDTO.TutorAtualizacaoDTO.class),
                            examples = @ExampleObject(name = "Exemplo válido", value = """
                                        {
                                             "nome": "Nome",
                                             "email": "tutor@email.com",
                                             "cpf": "111.111.111-11",
                                             "telefone": "(11) 11111-1111",
                                             "endereco": {
                                                "cep": "111111-11",
                                                "rua": "Rua Gandavo",
                                                "bairro": "Vila Mariana",
                                                "cidade": "São Paulo",
                                                "estado": "São Paulo",
                                                "numero": "11",
                                                "complemento": "Apartamento"
                                             }
                                         }
                                    """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Atualização realizada com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Tutor não encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Tutor não encontrado", value = "\"Tutor não encontrado ou inativo\""),
                                    }
                            )
                    )
            }
    )
    @PutMapping("/emailOuCpf/{emailOuCpf}")
    public ResponseEntity<TutorDTO.TutorResponseDTO> atualizarTutorPorEmailOuCpf(@PathVariable String emailOuCpf, @Valid @RequestBody TutorDTO.TutorAtualizacaoDTO dto) {
        return ResponseEntity.ok(tutorService.atualizarTutor(emailOuCpf, dto));
    }

    @Operation(
            summary = "Desativar um tutor",
            description = "Desativa um tutor da base de dados a partir do seu email",
            parameters = {
                    @Parameter(name = "emailOuCpf", description = "email ou cpf do tutor a ser desativado", example = "tutor@email.com")
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Tutor removido com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuário não encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Tutor não encontrado", value = "\"Tutor não encontrado ou inativo\""),
                                    }
                            )
                    )
            }
    )
    @DeleteMapping("/emailOuCpf/{emailOuCpf}")
    public ResponseEntity<Void> desativarTutor(@PathVariable String emailOuCpf) {
        tutorService.desativarTutor(emailOuCpf);
        return ResponseEntity.noContent().build();
    }
}
