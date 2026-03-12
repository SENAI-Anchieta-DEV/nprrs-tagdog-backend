package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.TutorDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Animal;
import com.senai.nprrs_tagdog_backend.domain.entity.Endereco;
import com.senai.nprrs_tagdog_backend.domain.entity.Tutor;
import com.senai.nprrs_tagdog_backend.domain.exceptions.*;
import com.senai.nprrs_tagdog_backend.domain.repository.AnimalRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.EnderecoRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public TutorDTO.TutorResponseDTO registrarTutor(TutorDTO.TutorRegistroDTO dto) {
        if(dto.animal() == null){
            throw new RegraNegocioException("Tutor deve possuir ao menos um animal");
        }
        if (tutorRepository.findByEmail(dto.email()) != null) {
            throw new EntidadeDuplicadaException("Tutor com este email");
        }
        if (tutorRepository.findByCpf(dto.cpf())!=null) {
            throw new EntidadeDuplicadaException("Tutor com este CPF");
        }

        Tutor tutor = dto.toEntity();
        tutor.setSenha(passwordEncoder.encode(dto.senha()));

        Animal animal = dto.animal().toEntity();

        String novaMatricula;
        do {
            novaMatricula = RandomStringUtils.randomNumeric(5);
        } while (animalRepository.existsByMatricula(novaMatricula) == true);

        animal.setMatricula("TD-" + novaMatricula);
        tutor.getAnimais().add(animal);

        tutorRepository.save(tutor);

        if (tutor.getAnimais().isEmpty()) {
            throw new OperacaoNaoPermitidaException("Nenhum animal vinculado.");
        }
        animalRepository.saveAll(tutor.getAnimais());

        if (dto.endereco() == null) {
            throw new DadosInvalidosException("Endereço é obrigatório.");
        }
        enderecoRepository.save(dto.endereco().toEntity());

        return TutorDTO.TutorResponseDTO.fromEntity(tutor);
    }

    @Transactional(readOnly = true)
    public List<TutorDTO.TutorResponseDTO> listarTutoresAtivos() {
        return tutorRepository.findAll()
                .stream()
                .map(TutorDTO.TutorResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TutorDTO.TutorResponseDTO buscarTutorPorEmailOuCpf(String emailOuCpf) {
        return TutorDTO.TutorResponseDTO.fromEntity(buscaDeTutorPorEmailOuCpf(emailOuCpf));
    }

    public TutorDTO.TutorResponseDTO atualizarTutor(String emailOuCpf, TutorDTO.TutorAtualizacaoDTO dto) {
        Tutor tutor = buscaDeTutorPorEmailOuCpf(emailOuCpf);

        tutor.setNome(dto.nome());
        tutor.setEmail(dto.email());
        tutor.setSenha(passwordEncoder.encode(dto.senha()));
        tutor.setCpf(dto.cpf());
        tutor.setTelefone(dto.telefone());

        Endereco endereco = enderecoRepository.save(dto.endereco().toEntity());
        tutor.setEndereco(endereco);

        return TutorDTO.TutorResponseDTO.fromEntity(tutorRepository.save(tutor));
    }

    public void desativarTutor(String emailOuCpf) {
        Tutor tutor = buscaDeTutorPorEmailOuCpf(emailOuCpf);

        if (tutor.isAtivo()){
            tutor.setAtivo(false);
            tutorRepository.save(tutor);
        } else {
            throw new ConflitosDeEstadoException("Tutor já está desativado");
        }

    }

    private Tutor buscaDeTutorPorEmailOuCpf(String emailOuCpf){
        if(tutorRepository.findByEmail(emailOuCpf) != null){
            return tutorRepository.findByEmail(emailOuCpf);
        } else if(tutorRepository.findByCpf(emailOuCpf) != null) {
            return tutorRepository.findByCpf(emailOuCpf);
        } else {
            throw new EntidadeNaoEncontradaException("Tutor");
        }
    }
}


