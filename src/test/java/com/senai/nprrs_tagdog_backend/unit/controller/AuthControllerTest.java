package com.senai.nprrs_tagdog_backend.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.nprrs_tagdog_backend.application.dto.AuthDTO;
import com.senai.nprrs_tagdog_backend.application.service.AuthService;
import com.senai.nprrs_tagdog_backend.infrastructure.security.JwtService;
import com.senai.nprrs_tagdog_backend.infrastructure.security.UsuarioDetailsService; // Novo Import
import com.senai.nprrs_tagdog_backend.interface_ui.controller.AuthController;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                com.senai.nprrs_tagdog_backend.infrastructure.security.SecurityConfig.class
                        }
                )
        }
)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    //Esses testes foram feitos por Sabrina Matos Almeida

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    // Mock 1: Necessário para o JwtAuthenticationFilter
    @MockBean
    private JwtService jwtService;

    // Mock 2: Também necessário para o JwtAuthenticationFilter (causa do seu último erro)
    @MockBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    @DisplayName("Deve realizar login")
    void deveRealizarLogin() throws Exception {

        AuthDTO.LoginRequest request =
                new AuthDTO.LoginRequest(
                        "admin@email.com",
                        "admin123"
                );

        when(authService.login(any()))
                .thenReturn("token-fake");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-fake"));
    }

    @Test
    @DisplayName("Não deve realizar login com senha inválida")
    void naoDeveRealizarLoginSenhaInvalida() throws Exception {

        AuthDTO.LoginRequest request =
                new AuthDTO.LoginRequest(
                        "admin@email.com",
                        "senhaErrada"
                );

        when(authService.login(any()))
                .thenThrow(new RuntimeException("Credenciais inválidas"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}