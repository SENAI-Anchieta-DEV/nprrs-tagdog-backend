package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.AnimalDTO;
import com.senai.nprrs_tagdog_backend.application.dto.TutorDTO;
import com.senai.nprrs_tagdog_backend.domain.repository.AnimalRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.EnderecoRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TutorService {
    private final TutorRepository tutorRepository;
    private final AnimalRepository animalRepository;
    private final EnderecoRepository enderecoRepository;
//    private final PasswordEncoder passwordEncoder;

    public TutorDTO.TutorResponseDTO registrarTutor(TutorDTO.TutorRegistroDTO dto) {
        if(dto.animais().isEmpty()){
            throw new RuntimeException(); //Animal nao encontrado
        }
        animalRepository.saveAll(dto.animais().stream().map(AnimalDTO.AnimalRegistroDTO::toEntity).toList());

        enderecoRepository.save(dto.endereco().toEntity());

//        tutor.setSenha(passwordEncoder.encode(dto.senha()));
        return TutorDTO.TutorResponseDTO.fromEntity(tutorRepository.save(dto.toEntity()));
    }
}
