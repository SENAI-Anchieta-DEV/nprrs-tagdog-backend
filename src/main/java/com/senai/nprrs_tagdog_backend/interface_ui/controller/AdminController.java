package com.senai.nprrs_tagdog_backend.interface_ui.controller;

import com.senai.nprrs_tagdog_backend.application.dto.AdminDTO;
import com.senai.nprrs_tagdog_backend.application.service.AdminService;
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

@Tag(name="Administrador",description = "gerenciamento do sistema")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @Operation(
            summary = "Cadastrar um novo administrador",
            description = "Realiza o cadastro do administrador",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AdminDTO.AdminRegistroDTO.class),
                            examples = @ExampleObject(name = "Exemplo válido", value = """
                                        {
                                             "nome": "Nome",
                                             "email": "admin@email.com",
                                             "senha": "senha"
                                         }
                                    """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cadastro realizado com sucesso")
            }
    )
    @PostMapping
    public ResponseEntity<AdminDTO.AdminResponseDTO> registrarAdmin(@Valid @RequestBody AdminDTO.AdminRegistroDTO dto) {
        AdminDTO.AdminResponseDTO novoAdmin = adminService.registrarAdmin(dto);
        return ResponseEntity.created(
                URI.create("/api/admin/email/" + novoAdmin.email())
        ).body(novoAdmin);
    }

    @Operation(
            summary = "Listar todos os administradores ativos",
            description = "Retorna todos os administradores cadastrados",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
            }
    )
    @GetMapping
    public ResponseEntity<List<AdminDTO.AdminResponseDTO>> listarAdminAtivos() {
        return ResponseEntity.ok(adminService.listarAdmiAtivos());
    }

    @Operation(
            summary = "Buscar um administrador ativo pelo email",
            description = "Retorna um administrador cadastrado",
            parameters = {
                    @Parameter(name = "email", description = "email do administrador a ser buscado", example = "admin@email.com")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Administrador retornado com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Administrador não encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "Administrador não encontrado", value = "\"Administrador não encontrado\"")
                            )
                    )
            }
    )
    @GetMapping("/email/{email}")
    public ResponseEntity<AdminDTO.AdminResponseDTO> buscarAdminAtivoPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(adminService.buscarAdminAtivoPorEmail(email));
    }

    @Operation(
            summary = "Atualizar um administrador ativo pelo email",
            description = "Realiza a atualização do administrador",
            parameters = {
                    @Parameter(name = "email", description = "email do administrador a ser atualizado", example = "admin@email.com")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AdminDTO.AdminRegistroDTO.class),
                            examples = @ExampleObject(name = "Exemplo válido", value = """
                                        {
                                             "nome": "Nome",
                                             "email": "admin@email.com",
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
                            description = "Administrador não encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Administrador não encontrado", value = "\"Administrador não encontrado\""),
                                    }
                            )
                    )
            }
    )
    @PutMapping("/email/{email}")
    public ResponseEntity<AdminDTO.AdminResponseDTO> atualizarAdminPorEmail(@PathVariable String email, @Valid @RequestBody AdminDTO.AdminRegistroDTO dto) {
        return ResponseEntity.ok(adminService.atualizarAdmin(email, dto));
    }

    @Operation(
            summary = "Desativar um administrador",
            description = "Desativa um administrador da base de dados a partir do seu email",
            parameters = {
                    @Parameter(name = "email", description = "email do administrador a ser desativado", example = "admin@email.com")
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Administrador removido com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuário não encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Administrador não encontrado", value = "\"Administrador não encontrado\""),
                                    }
                            )
                    )
            }
    )
    @DeleteMapping("/email/{email}")
    public ResponseEntity<Void> desativarAdmin(@PathVariable String email) {
        adminService.desativarAdmin(email);
        return ResponseEntity.noContent().build();
    }
}