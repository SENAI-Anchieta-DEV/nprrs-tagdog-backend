package com.senai.nprrs_tagdog_backend.integration.repository;

import com.senai.nprrs_tagdog_backend.domain.entity.Tutor;
import com.senai.nprrs_tagdog_backend.domain.entity.Role; // Importe adicionado
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
    //Devido a um erro de merge, o nome do autor foi sobrescrito. Esses testes foram feitos por Pietra Rainone Rocha

    @Autowired
    private TutorRepository repository;

    @Test
    @DisplayName("Deve buscar tutor por email com sucesso")
    void deveBuscarTutorPorEmail() {
        // Criando o objeto Tutor
        Tutor tutor = new Tutor();
        tutor.setNome("Maria Oliveira");
        tutor.setEmail("maria@email.com");
        tutor.setCpf("111.222.333-44");
        tutor.setSenha("senha123");

        // CORREÇÃO: Definindo a Role para não dar erro de coluna NULL no banco
        tutor.setRole(Role.TUTOR);

        // Definindo como ativo para garantir que a busca o encontre (se houver filtro de ativos)
        tutor.setAtivo(true);

        // Salvando no banco H2 de teste
        repository.save(tutor);

        // Executando o método que queremos testar
        Tutor encontrado = repository.findByEmail("maria@email.com");

        // Asserts (Verificações)
        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getNome()).isEqualTo("Maria Oliveira");
        assertThat(encontrado.getRole()).isEqualTo(Role.TUTOR);
    }

    @Test
    @DisplayName("Deve retornar null ao buscar email inexistente")
    void deveRetornarNullParaEmailInexistente() {
        // Testando o comportamento para um registro que não foi salvo
        Tutor encontrado = repository.findByEmail("naoexiste@email.com");
        assertThat(encontrado).isNull();
    }
}