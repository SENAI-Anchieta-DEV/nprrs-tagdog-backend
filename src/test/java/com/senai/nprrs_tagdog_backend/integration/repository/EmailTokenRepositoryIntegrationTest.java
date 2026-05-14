package com.senai.nprrs_tagdog_backend.integration.repository;

import com.senai.nprrs_tagdog_backend.domain.entity.EmailToken;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import com.senai.nprrs_tagdog_backend.domain.entity.Tutor;
import com.senai.nprrs_tagdog_backend.domain.entity.Usuario;
import com.senai.nprrs_tagdog_backend.domain.repository.EmailTokenRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EmailTokenRepositoryIntegrationTest {

    @Autowired
    private EmailTokenRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve buscar email token por usuário")
    void deveBuscarEmailTokenPorUsuario() {

        Usuario usuario = new Tutor();
        usuario.setNome("João");
        usuario.setEmail("joao@email.com");
        usuario.setSenha("senha123");
        usuario.setRole(Role.TUTOR);

        usuarioRepository.save(usuario);

        EmailToken token = new EmailToken();
        token.setToken(123456);
        token.setUsuario(usuario);
        token.setDataCriado(LocalDateTime.now());
        token.setDataExpirado(LocalDateTime.now().plusDays(1));

        repository.save(token);

        EmailToken resultado = repository.findByUsuario(usuario);

        assertNotNull(resultado);
        assertEquals(123456, resultado.getToken());
        assertEquals("joao@email.com", resultado.getUsuario().getEmail());
    }

    @Test
    @DisplayName("Deve retornar null ao buscar token de usuário inexistente")
    void deveRetornarNullParaUsuarioSemToken() {
        // Usa Tutor aqui também para poder instanciar
        Usuario usuarioSemToken = new Tutor();
        usuarioSemToken.setEmail("vazio@email.com");
        usuarioRepository.save(usuarioSemToken);

        EmailToken resultado = repository.findByUsuario(usuarioSemToken);

        assertNull(resultado);
    }
}