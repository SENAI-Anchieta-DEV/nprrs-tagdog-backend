package com.senai.nprrs_tagdog_backend.unit.repository;

import com.senai.nprrs_tagdog_backend.domain.entity.Admin;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import com.senai.nprrs_tagdog_backend.domain.entity.Usuario;
import com.senai.nprrs_tagdog_backend.domain.repository.UsuarioRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AuthRepositoryTest {

    @Autowired
    private UsuarioRepository usuarios;

    @Test
    @DisplayName("Deve buscar usuário ativo por email")
    void deveBuscarUsuarioAtivoPorEmail() {

        Usuario usuario = new Admin();

        usuario.setNome("Admin");
        usuario.setEmail("admin@email.com");
        usuario.setSenha("123456");
        usuario.setAtivo(true);
        usuario.setRole(Role.ADMIN);

        usuarios.save(usuario);

        Optional<Usuario> resultado =
                usuarios.findByEmailAndAtivoTrue("admin@email.com");

        assertTrue(resultado.isPresent());

        assertEquals(
                "admin@email.com",
                resultado.get().getEmail()
        );
    }

    @Test
    @DisplayName("Não deve buscar usuário inativo")
    void naoDeveBuscarUsuarioInativo() {

        Usuario usuario = new Admin();

        usuario.setNome("Admin");
        usuario.setEmail("admin@email.com");
        usuario.setSenha("123456");
        usuario.setAtivo(false);
        usuario.setRole(Role.ADMIN);

        usuarios.save(usuario);

        Optional<Usuario> resultado =
                usuarios.findByEmailAndAtivoTrue("admin@email.com");

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Não deve buscar usuário inexistente")
    void naoDeveBuscarUsuarioInexistente() {

        Optional<Usuario> resultado =
                usuarios.findByEmailAndAtivoTrue("naoexiste@email.com");

        assertTrue(resultado.isEmpty());
    }
}