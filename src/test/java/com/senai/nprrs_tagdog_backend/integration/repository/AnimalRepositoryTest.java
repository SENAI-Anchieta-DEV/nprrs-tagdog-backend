package com.senai.nprrs_tagdog_backend.integration.repository;

import com.senai.nprrs_tagdog_backend.domain.entity.Animal;
import com.senai.nprrs_tagdog_backend.domain.entity.PorteAnimal;
import com.senai.nprrs_tagdog_backend.domain.entity.SexoAnimal;
import com.senai.nprrs_tagdog_backend.domain.repository.AnimalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // Configura banco em memória e apenas JPA
class AnimalRepositoryTest {
    //Esses testes foram feitos por Rafael Borges Gibertoni

    @Autowired
    private AnimalRepository repository;

    @Autowired
    private TestEntityManager entityManager; // Auxilia a salvar dados sem depender do repository

    @Test
    @DisplayName("Deve encontrar um animal pela matrícula com sucesso")
    void deveEncontrarPorMatricula() {
        // GIVEN
        Animal animal = criarAnimal("TD-123", "999888");
        entityManager.persist(animal);

        // WHEN
        Optional<Animal> encontrado = repository.findByMatricula("TD-123");

        // THEN
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Bob");
    }

    @Test
    @DisplayName("Deve retornar verdadeiro quando a matrícula existir")
    void deveConfirmarExistenciaPorMatricula() {
        // GIVEN
        Animal animal = criarAnimal("TD-555", "111");
        entityManager.persist(animal);

        // WHEN
        boolean existe = repository.existsByMatricula("TD-555");

        // THEN
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve buscar animal pelo número da tag")
    void deveBuscarPorNumeroTag() {
        // GIVEN
        String tag = "TAG-XPTO";
        Animal animal = criarAnimal("TD-777", tag);
        entityManager.persist(animal);

        // WHEN
        Animal encontrado = repository.findByNumeroTag(tag);

        // THEN
        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getMatricula()).isEqualTo("TD-777");
    }

    @Test
    @DisplayName("Deve listar apenas animais que não possuem funcionário vinculado")
    void deveListarAnimaisSemFuncionario() {
        // GIVEN
        // Animal 1: Sem funcionário
        Animal animalLivre = criarAnimal("TD-LIVRE", "TAG-1");
        entityManager.persist(animalLivre);

        // Nota: Para testar o cenário com funcionário, você precisaria criar a entidade Funcionario,
        // associar ao animal e persistir. Aqui validamos se ele encontra o que está solto.

        // WHEN
        List<Animal> resultado = repository.findAnimaisSemFuncionario();

        // THEN
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).extracting(Animal::getMatricula).contains("TD-LIVRE");
    }

    // Helper para criar objeto Animal rapidamente
    private Animal criarAnimal(String matricula, String numeroTag) {
        return Animal.builder()
                .nome("Bob")
                .matricula(matricula)
                .numeroTag(numeroTag)
                .raca("Golden")
                .sexo(SexoAnimal.MACHO)
                .porte(PorteAnimal.GRANDE)
                .dataNascimento(LocalDate.now())
                .descricao("Teste")
                .ativo(true)
                .build();
    }
}