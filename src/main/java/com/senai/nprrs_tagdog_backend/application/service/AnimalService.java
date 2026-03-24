package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.AnimalDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Animal;
import com.senai.nprrs_tagdog_backend.domain.entity.Tutor;
import com.senai.nprrs_tagdog_backend.domain.exceptions.ConflitosDeEstadoException;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeNaoEncontradaException;
import com.senai.nprrs_tagdog_backend.domain.repository.AnimalRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository repository;
    private final TutorRepository tutorRepository;

    // CREATE
    public AnimalDTO.AnimalResponseDTO registrar(AnimalDTO.AnimalRegistroDTO dto, String emailOuCpfTutor) {

        Tutor tutor = new Tutor();
        if(tutorRepository.findByEmail(emailOuCpfTutor) != null){
            tutor = tutorRepository.findByEmail(emailOuCpfTutor);
        } else if(tutorRepository.findByCpf(emailOuCpfTutor) != null) {
            tutor = tutorRepository.findByCpf(emailOuCpfTutor);
        } else {
            throw new EntidadeNaoEncontradaException("Tutor");
        }

        Animal animal = dto.toEntity();

        String novaMatricula;
        do {
            novaMatricula = RandomStringUtils.randomNumeric(5);
        } while (repository.existsByMatricula(novaMatricula) == true);
        animal.setMatricula("TD-" + novaMatricula);

        repository.save(animal);
        tutor.getAnimais().add(animal);
        tutorRepository.save(tutor);

        return AnimalDTO.AnimalResponseDTO.fromEntity(animal, tutor);
    }

    // LISTAR SOMENTE ATIVOS
    public List<AnimalDTO.AnimalResponseDTO> listar() {

        return repository.findAll()
                .stream()
                .map(animal -> {
                    Tutor tutor = tutorRepository.findByAnimais(animal);
                    return AnimalDTO.AnimalResponseDTO.fromEntity(animal, tutor);
                })
                .toList();
    }

    public List<AnimalDTO.AnimalResponseDTO> listarAnimaisSemFuncionario() {

        return repository.findAnimaisSemFuncionario()
                .stream()
                .map(animal -> {
                    Tutor tutor = tutorRepository.findByAnimais(animal);
                    return AnimalDTO.AnimalResponseDTO.fromEntity(animal, tutor);
                })
                .toList();
    }

    // BUSCAR POR MATRICULA
    public AnimalDTO.AnimalResponseDTO buscarPorMatricula(String matricula) {

        Animal animal = repository.findByMatricula(matricula)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado"));

        Tutor tutor = tutorRepository.findByAnimais(animal);
        return AnimalDTO.AnimalResponseDTO.fromEntity(animal, tutor);
    }

    // ATUALIZAR
    public AnimalDTO.AnimalResponseDTO atualizar(String matricula, AnimalDTO.AnimalRegistroDTO dto) {

        Animal animal = repository.findByMatricula(matricula)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado"));
        Tutor tutor = tutorRepository.findByAnimais(animal);

        animal.setImagem(dto.imagem());
        animal.setNome(dto.nome());
        animal.setRaca(dto.raca());
        animal.setSexo(dto.sexo());
        animal.setPorte(dto.porte());
        animal.setDataNascimento(dto.dataNascimento());
        animal.setDescricao(dto.descricao());

        repository.save(animal);

        return AnimalDTO.AnimalResponseDTO.fromEntity(animal, tutor);
    }

    public AnimalDTO.AnimalResponseDTO tag(String matricula, String tag) {

        Animal animal = repository.findByMatricula(matricula)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado"));
        Tutor tutor = tutorRepository.findByAnimais(animal);

        animal.setNumeroTag(tag);
        repository.save(animal);

        return AnimalDTO.AnimalResponseDTO.fromEntity(animal, tutor);
    }

    // SOFT DELETE
    public void deletar(String matricula) {

        Animal animal = repository.findByMatricula(matricula)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Animal não encontrado"));

        if (animal.isAtivo()){
            animal.setAtivo(false);
            repository.save(animal);
        } else {
            animal.setAtivo(true);
            repository.save(animal);
        }

        repository.save(animal);
    }

}