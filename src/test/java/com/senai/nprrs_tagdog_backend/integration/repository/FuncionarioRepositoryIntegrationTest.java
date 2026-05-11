package com.senai.nprrs_tagdog_backend.integration.repository;

import com.senai.nprrs_tagdog_backend.domain.entity.*;
import com.senai.nprrs_tagdog_backend.domain.repository.FuncionarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FuncionarioRepositoryIntegrationTest {

    @Autowired
    private FuncionarioRepository repository;

    @Test
    @DisplayName("Deve salvar funcionário no banco")
    void deveSalvarFuncionarioNoBanco() {
        Funcionario funcionario = Funcionario.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senha("123456")
                .ativo(true)
                .role(Role.FUNCIONARIO)
                .animais(new ArrayList<>())
                .build();

        Funcionario salvo = repository.save(funcionario);

        assertNotNull(salvo.getId());
        assertEquals("João Silva", salvo.getNome());
        assertEquals("joao@email.com", salvo.getEmail());
        assertTrue(salvo.isAtivo());
    }

    @Test
    @DisplayName("Deve buscar funcionário por email")
    void deveBuscarFuncionarioPorEmail() {
        Funcionario funcionario = Funcionario.builder()
                .nome("Maria Souza")
                .email("maria@email.com")
                .senha("abcdef")
                .ativo(true)
                .role(Role.FUNCIONARIO)
                .animais(new ArrayList<>())
                .build();

        repository.save(funcionario);

        Optional<Funcionario> resultado = repository.findByEmail("maria@email.com");

        assertTrue(resultado.isPresent());
        assertEquals("Maria Souza", resultado.get().getNome());
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar email inexistente")
    void deveRetornarVazioAoBuscarEmailInexistente() {
        Optional<Funcionario> resultado = repository.findByEmail("naoexiste@email.com");

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve listar funcionários com animais")
    void deveListarFuncionariosComAnimais() {
        Animal animal = Animal.builder()
                .matricula("TD-12345")
                .nome("Rex")
                .raca("Raca")
                .sexo(SexoAnimal.MACHO)
                .porte(PorteAnimal.MEDIO)
                .dataNascimento(LocalDate.now())
                .descricao("Descricao")
                .ativo(true)
                .build();

        Funcionario funcionario = Funcionario.builder()
                .nome("Carlos")
                .email("carlos@email.com")
                .senha("123")
                .ativo(true)
                .role(Role.FUNCIONARIO)
                .animais(List.of(animal))
                .build();

        repository.save(funcionario);

        List<Funcionario> funcionarios = repository.findAll();

        assertFalse(funcionarios.isEmpty());
        assertEquals(1, funcionarios.get(0).getAnimais().size());
        assertEquals("Rex", funcionarios.get(0).getAnimais().get(0).getNome());
    }

    @Test
    @DisplayName("Deve buscar funcionário por animal")
    void deveBuscarFuncionarioPorAnimal() {
        Animal animal = Animal.builder()
                .matricula("TD-12345")
                .nome("Bolt")
                .raca("Raca")
                .sexo(SexoAnimal.MACHO)
                .porte(PorteAnimal.MEDIO)
                .dataNascimento(LocalDate.now())
                .descricao("Descricao")
                .ativo(true)
                .build();

        Funcionario funcionario = Funcionario.builder()
                .nome("Fernanda")
                .email("fernanda@email.com")
                .senha("senha")
                .ativo(true)
                .role(Role.FUNCIONARIO)
                .animais(List.of(animal))
                .build();

        repository.save(funcionario);

        Funcionario resultado = repository.findByAnimais(animal);

        assertNotNull(resultado);
        assertEquals("Fernanda", resultado.getNome());
    }
}