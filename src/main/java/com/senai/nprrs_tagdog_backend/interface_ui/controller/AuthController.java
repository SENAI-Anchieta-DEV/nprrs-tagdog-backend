package com.senai.nprrs_tagdog_backend.interface_ui.controller;

import com.senai.nprrs_tagdog_backend.application.dto.AuthDTO;
import com.senai.nprrs_tagdog_backend.application.service.AuthService;
import com.senai.nprrs_tagdog_backend.domain.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Login do usuário")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService auth;
    private final UsuarioRepository usuarios;

    @Operation(
            summary = "Logar um novo usuario",
            description = "Realiza o login do usuário e retorna um token de acesso",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AuthDTO.LoginRequest.class),
                            examples = @ExampleObject(name = "Exemplo válido", value = """
                                        {
                                          "email": "admin@email.com",
                                          "senha": "admin123"
                                        }
                                    """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuário não encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Usuário não encontrado", value = "\"Usuário não encontrado(a) ou inativo(a)\""),
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciais inválidas",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Credenciais inválidas", value = "\"Credenciais inválidas\""),
                                    }
                            )
                    )
            }
    )
    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<AuthDTO.TokenResponse> login(@Valid @RequestBody AuthDTO.LoginRequest req) {
        String token = auth.login(req);
        return ResponseEntity.ok(new AuthDTO.TokenResponse(token));
    }
}