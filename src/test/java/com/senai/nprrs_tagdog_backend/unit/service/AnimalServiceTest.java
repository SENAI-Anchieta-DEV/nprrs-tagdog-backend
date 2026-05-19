package com.senai.nprrs_tagdog_backend.unit.service;

import com.senai.nprrs_tagdog_backend.application.dto.AnimalDTO;
import com.senai.nprrs_tagdog_backend.application.service.AnimalService;
import com.senai.nprrs_tagdog_backend.domain.entity.Animal;
import com.senai.nprrs_tagdog_backend.domain.entity.CheckInCheckOut;
import com.senai.nprrs_tagdog_backend.domain.entity.CheckInOuCheckOut;
import com.senai.nprrs_tagdog_backend.domain.entity.Tutor;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeNaoEncontradaException;
import com.senai.nprrs_tagdog_backend.domain.repository.AnimalRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.CheckInCheckOutRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.TutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

    @ExtendWith(MockitoExtension.class)
    class AnimalServiceTest {
        //Esses testes foram feitos por Rafael Borges Gibertoni

        @Mock
        private AnimalRepository animalRepository;

        @Mock
        private TutorRepository tutorRepository;

        @Mock
        private CheckInCheckOutRepository checkInCheckOutRepository;

        @InjectMocks
        private AnimalService animalService;

        private Animal animalMock;
        private Tutor tutorMock;
        private AnimalDTO.AnimalRegistroDTO registroDTOMock;

        @BeforeEach
        void setUp() {
            // Inicialização de objetos comuns para os testes
            tutorMock = new Tutor();
            tutorMock.setId(String.valueOf(1L));
            tutorMock.setAnimais(new ArrayList<>());
            tutorMock.setAtivo(true);

            animalMock = new Animal();
            animalMock.setId(String.valueOf(1L));
            animalMock.setMatricula("TD-12345");
            animalMock.setNome("Rex");
            animalMock.setCheckInCheckOut(new ArrayList<>());
            animalMock.setAtivo(true);

            registroDTOMock = mock(AnimalDTO.AnimalRegistroDTO.class);
            lenient().when(registroDTOMock.toEntity()).thenReturn(animalMock);
        }

        // --- TESTES DE REGISTRAR ---

        @Test
        @DisplayName("Deve registrar um animal quando tutor for encontrado por e-mail")
        void registrar_Sucesso_TutorPorEmail() {
            String email = "tutor@teste.com";
            when(tutorRepository.findByEmail(email)).thenReturn(tutorMock);
            when(animalRepository.existsByMatricula(anyString())).thenReturn(false);

            AnimalDTO.AnimalResponseDTO response = animalService.registrar(registroDTOMock, email, any());

            assertNotNull(response);
            verify(animalRepository).save(any(Animal.class));
            verify(tutorRepository).save(tutorMock);
            assertTrue(animalMock.getMatricula().startsWith("TD-"));
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar registrar animal para tutor inexistente")
        void registrar_Erro_TutorNaoEncontrado() {
            String identificadorInvalido = "inexistente";
            when(tutorRepository.findByEmail(identificadorInvalido)).thenReturn(null);
            when(tutorRepository.findByCpf(identificadorInvalido)).thenReturn(null);

            assertThrows(EntidadeNaoEncontradaException.class, () -> {
                animalService.registrar(registroDTOMock, identificadorInvalido, any());
            });

            verify(animalRepository, never()).save(any(Animal.class));
        }

        // --- TESTES DE BUSCAR E LISTAR ---

        @Test
        @DisplayName("Deve buscar animal por matrícula com sucesso")
        void buscarPorMatricula_Sucesso() {
            when(animalRepository.findByMatricula("TD-12345")).thenReturn(Optional.of(animalMock));
            when(tutorRepository.findByAnimais(animalMock)).thenReturn(tutorMock);

            AnimalDTO.AnimalResponseDTO response = animalService.buscarPorMatricula("TD-12345");

            assertNotNull(response);
            verify(animalRepository).findByMatricula("TD-12345");
        }

        @Test
        @DisplayName("Deve lançar RuntimeException ao buscar matrícula inexistente")
        void buscarPorMatricula_NaoEncontrado() {
            when(animalRepository.findByMatricula("TD-99999")).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> {
                animalService.buscarPorMatricula("TD-99999");
            });
        }

        // --- TESTES DE CHECK-IN E CHECK-OUT ---

        @Test
        @DisplayName("Deve registrar CHECK_IN quando histórico estiver vazio")
        void checkInOuCheckOut_FazerCheckIn() {
            when(animalRepository.findByMatricula("TD-12345")).thenReturn(Optional.of(animalMock));
            when(tutorRepository.findByAnimais(animalMock)).thenReturn(tutorMock);

            animalService.checkInOuCheckOut("TD-12345");

            verify(checkInCheckOutRepository).save(argThat(checkInOut ->
                    checkInOut.getCheckInOuCheckOut() == CheckInOuCheckOut.CHECK_IN
            ));
            verify(animalRepository).save(animalMock);
        }

        @Test
        @DisplayName("Deve registrar CHECK_OUT quando o último registro for CHECK_IN")
        void checkInOuCheckOut_FazerCheckOut() {
            CheckInCheckOut ultimoRegistro = new CheckInCheckOut();
            ultimoRegistro.setCheckInOuCheckOut(CheckInOuCheckOut.CHECK_IN);
            animalMock.getCheckInCheckOut().add(ultimoRegistro);

            when(animalRepository.findByMatricula("TD-12345")).thenReturn(Optional.of(animalMock));
            when(tutorRepository.findByAnimais(animalMock)).thenReturn(tutorMock);

            animalService.checkInOuCheckOut("TD-12345");

            verify(checkInCheckOutRepository).save(argThat(checkInOut ->
                    checkInOut.getCheckInOuCheckOut() == CheckInOuCheckOut.CHECK_OUT
            ));
        }

        // --- TESTES DE DELETAR (ATIVAR/DESATIVAR) ---

        @Test
        @DisplayName("Deve desativar o animal se ele estiver ativo")
        void deletar_DesativarAnimal() {
            animalMock.setAtivo(true);
            when(animalRepository.findByMatricula("TD-12345")).thenReturn(Optional.of(animalMock));

            animalService.deletar("TD-12345");

            assertFalse(animalMock.isAtivo());
            verify(animalRepository, times(2)).save(animalMock); // Salva no if e fora do if
        }

        @Test
        @DisplayName("Deve reativar o animal se ele estiver inativo e o tutor estiver ativo")
        void deletar_ReativarAnimal() {
            animalMock.setAtivo(false);
            tutorMock.setAtivo(true);
            when(animalRepository.findByMatricula("TD-12345")).thenReturn(Optional.of(animalMock));
            when(tutorRepository.findByAnimais(animalMock)).thenReturn(tutorMock);

            animalService.deletar("TD-12345");

            assertTrue(animalMock.isAtivo());
            verify(animalRepository, times(2)).save(animalMock); // Salva no if e fora do if
        }
    }

