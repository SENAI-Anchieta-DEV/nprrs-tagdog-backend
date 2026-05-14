package com.senai.nprrs_tagdog_backend.integration.repository;

import com.senai.nprrs_tagdog_backend.domain.entity.Admin;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import com.senai.nprrs_tagdog_backend.domain.repository.AdminRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AdminRepositoryIntegrationTest {
    //Devido a um erro de merge, o nome do autor foi sobrescrito. Esses testes foram feitos por Sabrina Matos Almeida

    @Autowired
    private AdminRepository repository;

    @Test
    @DisplayName("Deve buscar admin por email")
    void deveBuscarAdminPorEmail() {

        Admin admin = new Admin();

        admin.setNome("Sabrina");
        admin.setEmail("sabrina@email.com");
        admin.setSenha("123456");
        admin.setRole(Role.ADMIN);

        repository.save(admin);

        Optional<Admin> resultado =
                repository.findByEmail(
                        "sabrina@email.com"
                );

        assertTrue(resultado.isPresent());

        assertEquals(
                "sabrina@email.com",
                resultado.get().getEmail()
        );
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar email inexistente")
    void deveRetornarVazioAoBuscarEmailInexistente() {

        Optional<Admin> resultado =
                repository.findByEmail(
                        "naoexiste@email.com"
                );

        assertTrue(resultado.isEmpty());
    }
}