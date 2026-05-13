package com.senai.nprrs_tagdog_backend.integration.repository;

import com.senai.nprrs_tagdog_backend.domain.entity.Tag;
import com.senai.nprrs_tagdog_backend.domain.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TagRepositoryIntegrationTest {

    @Autowired
    private TagRepository tagRepository;

    @Test
    @DisplayName("Deve salvar tag no banco")
    void deveSalvarTagNoBanco() {
        Tag tag = Tag.builder()
                .numero("TAG-001")
                .latitude("-23.550520")
                .longitude("-46.633308")
                .dataCriado(LocalDateTime.now())
                .ativo(true)
                .saidaNaoAutorizada(false)
                .build();

        Tag salvo = tagRepository.save(tag);

        assertNotNull(salvo.getId());
        assertEquals("TAG-001", salvo.getNumero());
        assertTrue(salvo.isAtivo());
    }

    @Test
    @DisplayName("Deve buscar a última tag por numero")
    void deveBuscarUltimaTagPorNumero() {
        Tag tag1 = Tag.builder().numero("TAG-002").latitude("0").longitude("0")
                .dataCriado(LocalDateTime.now().minusDays(2)).ativo(true).saidaNaoAutorizada(false).build();
        Tag tag2 = Tag.builder().numero("TAG-002").latitude("1").longitude("1")
                .dataCriado(LocalDateTime.now().minusDays(1)).ativo(true).saidaNaoAutorizada(false).build();
        Tag tag3 = Tag.builder().numero("TAG-002").latitude("2").longitude("2")
                .dataCriado(LocalDateTime.now()).ativo(true).saidaNaoAutorizada(false).build();

        tagRepository.save(tag1);
        tagRepository.save(tag2);
        tagRepository.save(tag3);

        Optional<Tag> ultimaTag = tagRepository.findFirstByNumeroOrderByDataCriadoDesc("TAG-002");

        assertTrue(ultimaTag.isPresent());
        assertEquals("2", ultimaTag.get().getLatitude());
        assertEquals("2", ultimaTag.get().getLongitude());
    }

    @Test
    @DisplayName("Deve buscar últimas posições de cada tag")
    void deveBuscarUltimasPosicoesDeCadaTag() {
        tagRepository.save(Tag.builder().numero("TAG-A").latitude("0").longitude("0")
                .dataCriado(LocalDateTime.now().minusDays(2)).ativo(true).saidaNaoAutorizada(false).build());
        tagRepository.save(Tag.builder().numero("TAG-A").latitude("1").longitude("1")
                .dataCriado(LocalDateTime.now()).ativo(true).saidaNaoAutorizada(false).build());

        tagRepository.save(Tag.builder().numero("TAG-B").latitude("10").longitude("10")
                .dataCriado(LocalDateTime.now()).ativo(true).saidaNaoAutorizada(false).build());

        List<Tag> ultimasPosicoes = tagRepository.findUltimasPosicoesDeCadaTag();

        assertEquals(2, ultimasPosicoes.size());

        ultimasPosicoes.forEach(tag -> {
            if (tag.getNumero().equals("TAG-A")) {
                assertEquals("1", tag.getLatitude());
            } else if (tag.getNumero().equals("TAG-B")) {
                assertEquals("10", tag.getLatitude());
            } else {
                fail("Número de tag inesperado: " + tag.getNumero());
            }
        });
    }
}