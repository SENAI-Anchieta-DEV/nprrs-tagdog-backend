package com.senai.nprrs_tagdog_backend.unit.service;

import com.senai.nprrs_tagdog_backend.application.dto.AuthDTO;
import com.senai.nprrs_tagdog_backend.application.service.AuthService;
import com.senai.nprrs_tagdog_backend.domain.entity.Admin;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import com.senai.nprrs_tagdog_backend.domain.entity.Usuario;
import com.senai.nprrs_tagdog_backend.domain.exceptions.DadosInvalidosException;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeNaoEncontradaException;
import com.senai.nprrs_tagdog_backend.domain.repository.UsuarioRepository;
import com.senai.nprrs_tagdog_backend.infrastructure.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    //Esses testes foram feitos por Sabrina Matos Almeida

    @InjectMocks
    private AuthService authService;

    @Mock
    private UsuarioRepository usuarios;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtService jwt;

    private Usuario usuario;

    @BeforeEach
    void setup() {

        usuario = new Admin();

        usuario.setNome("Admin");
        usuario.setEmail("admin@email.com");
        usuario.setSenha("123456");
        usuario.setAtivo(true);
        usuario.setRole(Role.ADMIN);
    }

    @Test
    @DisplayName("Deve realizar login")
    void deveRealizarLogin() {

        AuthDTO.LoginRequest request =
                new AuthDTO.LoginRequest(
                        "admin@email.com",
                        "123456"
                );

        when(usuarios.findByEmailAndAtivoTrue("admin@email.com"))
                .thenReturn(Optional.of(usuario));

        when(encoder.matches("123456", "123456"))
                .thenReturn(true);

        when(jwt.generateToken(
                usuario.getEmail(),
                usuario.getRole().name()
        )).thenReturn("token-fake");

        String token = authService.login(request);

        assertNotNull(token);
        assertEquals("token-fake", token);

        verify(usuarios)
                .findByEmailAndAtivoTrue("admin@email.com");

        verify(encoder)
                .matches("123456", "123456");

        verify(jwt)
                .generateToken(
                        usuario.getEmail(),
                        usuario.getRole().name()
                );
    }

    @Test
    @DisplayName("Não deve realizar login com senha inválida")
    void naoDeveRealizarLoginSenhaInvalida() {

        AuthDTO.LoginRequest request =
                new AuthDTO.LoginRequest(
                        "admin@email.com",
                        "senha-errada"
                );

        when(usuarios.findByEmailAndAtivoTrue("admin@email.com"))
                .thenReturn(Optional.of(usuario));

        when(encoder.matches("senha-errada", "123456"))
                .thenReturn(false);

        assertThrows(
                DadosInvalidosException.class,
                () -> authService.login(request)
        );
    }

    @Test
    @DisplayName("Não deve realizar login com usuário inexistente")
    void naoDeveRealizarLoginUsuarioInexistente() {

        AuthDTO.LoginRequest request =
                new AuthDTO.LoginRequest(
                        "naoexiste@email.com",
                        "123456"
                );

        when(usuarios.findByEmailAndAtivoTrue("naoexiste@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                EntidadeNaoEncontradaException.class,
                () -> authService.login(request)
        );
    }
}