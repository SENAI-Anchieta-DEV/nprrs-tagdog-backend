package com.senai.nprrs_tagdog_backend.interface_ui.controller;

import com.senai.nprrs_tagdog_backend.application.dto.FuncionarioDTO;
import com.senai.nprrs_tagdog_backend.application.service.FuncionarioService;
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

@Tag(name = "Funcionario", description = "Gerenciamento de funcionarios")
@RestController
@RequestMapping("/api/funcionarios")
@RequiredArgsConstructor
public class FuncionarioController {
    private final FuncionarioService funcionarioService;

    @Operation(
            summary = "Cadastrar um novo funcionario",
            description = "Realiza o cadastro do funcionario",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = FuncionarioDTO.FuncionarioRegistroDTO.class),
                            examples = @ExampleObject(name = "Exemplo válido", value = """
                                        {
                                             "nome": "Nome",
                                             "email": "funcionario@email.com",
                                             "senha": "senha"
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
    public ResponseEntity<FuncionarioDTO.FuncionarioResponseDTO> registrarFuncionario(@Valid @RequestBody FuncionarioDTO.FuncionarioRegistroDTO dto) {
        FuncionarioDTO.FuncionarioResponseDTO novoFuncionario = funcionarioService.registrarFuncionario(dto);
        return ResponseEntity.created(
                URI.create("/api/funcionarios/email/" + novoFuncionario.email())
        ).body(novoFuncionario);
    }

    @Operation(
            summary = "Listar todos os funcionarios ativos",
            description = "Retorna todos os funcionarios cadastrados",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
            }
    )
    @GetMapping
    public ResponseEntity<List<FuncionarioDTO.FuncionarioResponseDTO>> listarFuncinariosAtivos() {
        return ResponseEntity.ok(funcionarioService.listarFuncionariosAtivos());
    }

    @Operation(
            summary = "Buscar um funcionario ativo pelo email",
            description = "Retorna um funcionario cadastrado",
            parameters = {
                    @Parameter(name = "email", description = "email do funcionario a ser buscado", example = "funcionario@email.com")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Funcionario retornado com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Funcionario não encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "Funcionario não encontrado", value = "\"Funcionario com email funcionario@email.com não encontrado.\"")
                            )
                    )
            }
    )
    @GetMapping("/email/{email}")
    public ResponseEntity<FuncionarioDTO.FuncionarioResponseDTO> buscarFuncionarioAtivoPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(funcionarioService.buscarFuncionarioAtivoPorEmail(email));
    }

    @Operation(
            summary = "Atualizar um funcionario ativo pelo email",
            description = "Realiza a atualização do funcionario",
            parameters = {
                    @Parameter(name = "email", description = "email do funcionario a ser atualizado", example = "funcionario@email.com")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = FuncionarioDTO.FuncionarioRegistroDTO.class),
                            examples = @ExampleObject(name = "Exemplo válido", value = """
                                        {
                                             "nome": "Nome",
                                             "email": "funcionario@email.com",
                                             "senha": "senha"
                                         }
                                    """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Atualização realizada com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Funcionario não encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Funcionario não encontrado", value = "\"Funcionario não encontrado ou inativo\""),
                                    }
                            )
                    )
            }
    )
    @PutMapping("/email/{email}")
    public ResponseEntity<FuncionarioDTO.FuncionarioResponseDTO> atualizarFuncionarioPorEmail(@PathVariable String email, @Valid @RequestBody FuncionarioDTO.FuncionarioRegistroDTO dto) {
        return ResponseEntity.ok(funcionarioService.atualizarFuncionario(email, dto));
    }

    @Operation(
            summary = "Desativar um funcionario",
            description = "Desativa um funcionario da base de dados a partir do seu email",
            parameters = {
                    @Parameter(name = "email", description = "email do funcionario a ser desativado", example = "funcionario@email.com")
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Funcionario removido com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuário não encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Funcionario não encontrado", value = "\"Funcionario não encontrado ou inativo\""),
                                    }
                            )
                    )
            }
    )
    @DeleteMapping("/email/{email}")
    public ResponseEntity<Void> desativarFuncionario(@PathVariable String email) {
        funcionarioService.desativarFuncionario(email);
        return ResponseEntity.noContent().build();
    }
}
