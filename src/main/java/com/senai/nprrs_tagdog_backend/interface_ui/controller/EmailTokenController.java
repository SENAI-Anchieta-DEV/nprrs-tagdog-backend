package com.senai.nprrs_tagdog_backend.interface_ui.controller;

import com.senai.nprrs_tagdog_backend.application.dto.EmailTokenDTO;
import com.senai.nprrs_tagdog_backend.application.dto.SenhaDTO;
import com.senai.nprrs_tagdog_backend.application.service.EmailTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "EmailToken", description = "Gerenciamento de tokens mandados pelo email")
@RestController
@RequestMapping("/api/emailtoken")
@RequiredArgsConstructor
public class EmailTokenController {
    private final EmailTokenService emailTokenService;

    @Operation(
            summary = "Criar um novo token ou atualizar token",
            description = "Realiza a criacao ou atualizacao do token e o manda pelo email",
            parameters = {
                    @Parameter(name = "email", description = "email do usuario a ser buscado", example = "tutor@email.com")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cadastro realizado com sucesso")
            }
    )
    @PostMapping("/email/{email}")
    public ResponseEntity<EmailTokenDTO> registrarFuncionario(@PathVariable String email) {
        EmailTokenDTO novoEmailToken = emailTokenService.criarTokenEMandarEmail(email);
        return ResponseEntity.created(
                URI.create("/api/emailtoken/token/" + novoEmailToken.token())
        ).body(novoEmailToken);
    }

    @Operation(
            summary = "Validar um token para alterar senha",
            description = "Realiza a validacao do token e altera a senha do usuario",
            parameters = {
                    @Parameter(name = "email", description = "email do usuario a ser buscado", example = "tutor@email.com"),
                    @Parameter(name = "token", description = "token do usuario", example = "123456")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token validado com sucesso")
            }
    )
    @PutMapping
    public ResponseEntity<Void> validarEmailToken(@Valid @RequestBody SenhaDTO dto) {
        emailTokenService.validarEmailToken(dto);
        return ResponseEntity.noContent().build();
    }
}
