package com.senai.nprrs_tagdog_backend.integration.repository;

import com.senai.nprrs_tagdog_backend.domain.entity.LocalCoordenadas;
import com.senai.nprrs_tagdog_backend.domain.repository.LocalCoordenadasRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LocalCoordenadasRepositoryIntegrationTest {
    //Devido a um erro de merge, o nome do autor foi sobrescrito. Esses testes foram feitos por Nicoly de Oliveira Machado

    @Autowired
    private LocalCoordenadasRepository repository;

    @Test
    @DisplayName("Deve buscar local coordenadas por CEP")
    void deveBuscarLocalPorCep() {
        // 1. Criar e persistir o LocalCoordenadas
        // Usei o Builder que está presente na sua entidade
        LocalCoordenadas local = LocalCoordenadas.builder()
                .cep("04023-001")
                .latitude("-23.591348")
                .longitude("-46.645165")
                .raio(10)
                .build();

        repository.save(local);

        // 2. Executar o método de busca do repository
        Optional<LocalCoordenadas> resultado = repository.findByCep("04023-001");

        // 3. Assertivas
        assertTrue(resultado.isPresent());
        assertEquals("04023-001", resultado.get().getCep());
        assertEquals("-23.591348", resultado.get().getLatitude());
        assertEquals(10, resultado.get().getRaio());
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar CEP não cadastrado")
    void deveRetornarVazioParaCepInexistente() {
        // Executar busca de um CEP que nunca foi salvo
        Optional<LocalCoordenadas> resultado = repository.findByCep("00000-000");

        // Assertiva
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve deletar todas as coordenadas")
    void deveDeletarTudo() {
        // 1. Salvar um registro
        LocalCoordenadas local = LocalCoordenadas.builder()
                .cep("11111-111")
                .latitude("0.0")
                .longitude("0.0")
                .raio(5)
                .build();
        repository.save(local);

        // 2. Deletar todos (simulando a lógica do seu Service)
        repository.deleteAll();

        // 3. Verificar se o banco está vazio
        assertEquals(0, repository.count());
    }
}