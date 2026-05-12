package com.senai.nprrs_tagdog_backend.unit.service;

import com.senai.nprrs_tagdog_backend.application.dto.SenhaDTO;
import com.senai.nprrs_tagdog_backend.application.service.EmailTokenService;
import com.senai.nprrs_tagdog_backend.domain.entity.EmailToken;
import com.senai.nprrs_tagdog_backend.domain.entity.Tutor;
import com.senai.nprrs_tagdog_backend.domain.entity.Usuario;
import com.senai.nprrs_tagdog_backend.domain.exceptions.RegraNegocioException;
import com.senai.nprrs_tagdog_backend.domain.repository.EmailTokenRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

// CORREÇÃO DOS IMPORTS AQUI:
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailTokenServiceTest {

    @Mock
    private EmailTokenRepository emailTokenRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmailTokenService service;

    private SenhaDTO senhaDTO;
    private Usuario usuario;
    private EmailToken emailToken;

    @BeforeEach
    void setUp() {
        String email = "tutor@email.com";
        String tokenValido = "123456";

        senhaDTO = new SenhaDTO(email, tokenValido, "novaSenha123");

        usuario = new Tutor();
        usuario.setEmail(email);
        usuario.setSenha("senhaAntiga");

        emailToken = new EmailToken();
        emailToken.setToken(Integer.parseInt(tokenValido));
        emailToken.setUsuario(usuario);
        emailToken.setDataExpirado(LocalDateTime.now().plusDays(1));
    }

    @Test
    @DisplayName("Deve validar token e alterar senha com sucesso")
    void deveValidarTokenEAlterarSenha() {
        when(usuarioRepository.findByEmail(senhaDTO.email()))
                .thenReturn(Optional.of(usuario));

        when(emailTokenRepository.findByUsuario(usuario))
                .thenReturn(emailToken);

        when(passwordEncoder.encode(senhaDTO.senha()))
                .thenReturn("senhaCriptografada");

        assertDoesNotThrow(() -> service.validarEmailToken(senhaDTO));

        // Agora o verify vai funcionar porque o import está correto
        verify(usuarioRepository, times(1)).save(usuario);
        verify(emailTokenRepository, times(1)).delete(emailToken);
        assertEquals("senhaCriptografada", usuario.getSenha());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o token estiver incorreto")
    void deveLancarExcecaoQuandoTokenIncorreto() {
        SenhaDTO dtoComTokenErrado = new SenhaDTO("tutor@email.com", "000000", "senha");

        when(usuarioRepository.findByEmail(dtoComTokenErrado.email()))
                .thenReturn(Optional.of(usuario));

        when(emailTokenRepository.findByUsuario(usuario))
                .thenReturn(emailToken);

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () ->
                service.validarEmailToken(dtoComTokenErrado)
        );

        assertEquals("EmailToken inválido", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o token estiver expirado")
    void deveLancarExcecaoQuandoTokenExpirado() {
        emailToken.setDataExpirado(LocalDateTime.now().minusHours(1));

        when(usuarioRepository.findByEmail(senhaDTO.email()))
                .thenReturn(Optional.of(usuario));

        when(emailTokenRepository.findByUsuario(usuario))
                .thenReturn(emailToken);

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () ->
                service.validarEmailToken(senhaDTO)
        );

        assertEquals("Data do EmailToken expirada", exception.getMessage());
        verify(emailTokenRepository, never()).delete(any());
    }
}