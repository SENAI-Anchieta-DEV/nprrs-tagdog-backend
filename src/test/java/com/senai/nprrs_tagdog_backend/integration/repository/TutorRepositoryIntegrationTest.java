package com.senai.nprrs_tagdog_backend.integration.repository;

import com.senai.nprrs_tagdog_backend.domain.entity.Tutor;
import com.senai.nprrs_tagdog_backend.domain.repository.TutorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TutorRepositoryIntegrationTest {

    @Autowired
    private TutorRepository repository;

    @Test
    @DisplayName("Deve buscar tutor por email com sucesso")
    void deveBuscarTutorPorEmail() {
        Tutor tutor = new Tutor();
        tutor.setNome("Maria Oliveira");
        tutor.setEmail("maria@email.com");
        tutor.setCpf("111.222.333-44");
        tutor.setSenha("senha123");
        repository.save(tutor);

        Tutor encontrado = repository.findByEmail("maria@email.com");

        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getNome()).isEqualTo("Maria Oliveira");
    }

    @Test
    @DisplayName("Deve retornar null ao buscar email inexistente")
    void deveRetornarNullParaEmailInexistente() {
        Tutor encontrado = repository.findByEmail("naoexiste@email.com");
        assertThat(encontrado).isNull();
    }
}