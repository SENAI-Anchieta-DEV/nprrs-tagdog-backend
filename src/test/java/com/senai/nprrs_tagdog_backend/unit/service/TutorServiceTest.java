package com.senai.nprrs_tagdog_backend.unit.service;

import com.senai.nprrs_tagdog_backend.application.dto.TutorDTO;
import com.senai.nprrs_tagdog_backend.application.service.TutorService;
import com.senai.nprrs_tagdog_backend.domain.exceptions.RegraNegocioException;
import com.senai.nprrs_tagdog_backend.domain.repository.AnimalRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.EnderecoRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.TutorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutorServiceTest {
    //Devido a um erro de merge, o nome do autor foi sobrescrito. Esses testes foram feitos por Pietra Rainone Rocha

    @Mock
    private TutorRepository tutorRepository;
    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private EnderecoRepository enderecoRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TutorService tutorService;

    @Test
    @DisplayName("Deve lançar exceção quando tutor não possui animal no registro")
    void deveLancarExcecaoTutorSemAnimal() {

        TutorDTO.TutorRegistroDTO dto = new TutorDTO.TutorRegistroDTO(
                "Nome", "email@email.com", "cpf", "tel", "senha", null, null
        );

        assertThrows(RegraNegocioException.class, () -> {
            tutorService.registrarTutor(dto, null);
        });


        verifyNoInteractions(tutorRepository);
    }
}