package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.TutorDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Tutor;
import com.senai.nprrs_tagdog_backend.domain.repository.AnimalRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.EnderecoRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
            throw new RuntimeException(); //Entidade animal nao encontrado
        }

//        tutor.setSenha(passwordEncoder.encode(dto.senha()));
        Tutor tutor = tutorRepository.save(dto.toEntity());
        animalRepository.saveAll(tutor.getAnimais());
        enderecoRepository.save(dto.endereco().toEntity());

        return TutorDTO.TutorResponseDTO.fromEntity(tutor);
    }

    @Transactional(readOnly = true)
    public List<TutorDTO.TutorResponseDTO> listarTutoresAtivos() {
        return tutorRepository.findAllByAtivoTrue()
                .stream()
                .map(TutorDTO.TutorResponseDTO::fromEntity)
                .toList();
    }
}
