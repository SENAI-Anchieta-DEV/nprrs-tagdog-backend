package com.senai.nprrs_tagdog_backend.unit.service;

import com.senai.nprrs_tagdog_backend.application.dto.LocalCoordenadasDTO;
import com.senai.nprrs_tagdog_backend.application.service.LocalCoordenadasService;
import com.senai.nprrs_tagdog_backend.domain.entity.LocalCoordenadas;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeNaoEncontradaException;
import com.senai.nprrs_tagdog_backend.domain.repository.LocalCoordenadasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocalCoordenadasServiceTest {

    @Mock
    private LocalCoordenadasRepository localCoordenadasRepository;

    @InjectMocks
    private LocalCoordenadasService service;

    private LocalCoordenadasDTO localDTO;
    private LocalCoordenadas localEntidade;

    @BeforeEach
    void setUp() {
        localDTO = new LocalCoordenadasDTO(
                "04023-001",
                "-23.591348",
                "-46.645165",
                10
        );

        localEntidade = LocalCoordenadas.builder()
                .id("uuid-123")
                .cep("04023-001")
                .latitude("-23.591348")
                .longitude("-46.645165")
                .raio(10)
                .build();
    }

    @Test
    @DisplayName("Deve registrar local limpando registros anteriores se existirem")
    void deveRegistrarLocalLimpandoAnteriores() {
        // Mock do if(!findAll().isEmpty())
        when(localCoordenadasRepository.findAll()).thenReturn(List.of(localEntidade));
        when(localCoordenadasRepository.save(any(LocalCoordenadas.class))).thenReturn(localEntidade);

        LocalCoordenadasDTO resultado = service.registrar(localDTO);

        assertNotNull(resultado);
        assertEquals(localDTO.cep(), resultado.cep());

        verify(localCoordenadasRepository, atLeastOnce()).deleteAll(any());
        verify(localCoordenadasRepository).save(any(LocalCoordenadas.class));
    }

    @Test
    @DisplayName("Deve buscar o primeiro local encontrado")
    void deveBuscarLocal() {
        when(localCoordenadasRepository.findAll()).thenReturn(List.of(localEntidade));

        LocalCoordenadasDTO resultado = service.buscar();

        assertNotNull(resultado);
        assertEquals("04023-001", resultado.cep());

        verify(localCoordenadasRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar e não houver registros")
    void deveLancarExcecaoAoBuscarVazio() {
        when(localCoordenadasRepository.findAll()).thenReturn(List.of());

        assertThrows(EntidadeNaoEncontradaException.class, () -> service.buscar());
    }

    @Test
    @DisplayName("Deve deletar local por CEP com sucesso")
    void deveDeletarLocalPorCep() {
        String cep = "04023-001";
        when(localCoordenadasRepository.findByCep(cep)).thenReturn(Optional.of(localEntidade));

        assertDoesNotThrow(() -> service.deletar(cep));

        verify(localCoordenadasRepository, times(1)).delete(localEntidade);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar CEP inexistente")
    void deveLancarExcecaoAoDeletarInexistente() {
        String cepErrado = "00000-000";
        when(localCoordenadasRepository.findByCep(cepErrado)).thenReturn(Optional.empty());

        assertThrows(EntidadeNaoEncontradaException.class, () -> service.deletar(cepErrado));
        verify(localCoordenadasRepository, never()).delete(any());
    }
}