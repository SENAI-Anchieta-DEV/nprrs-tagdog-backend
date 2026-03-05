package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.AnimalDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Animal;
import com.senai.nprrs_tagdog_backend.domain.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository repository;

    // CREATE
    public AnimalDTO.AnimalResponseDTO registrar(AnimalDTO.AnimalRegistroDTO dto) {

        Animal animal = dto.toEntity();

        repository.save(animal);

        return AnimalDTO.AnimalResponseDTO.fromEntity(animal);
    }

    // LISTAR SOMENTE ATIVOS
    public List<AnimalDTO.AnimalResponseDTO> listar() {

        return repository.findAllByAtivoTrue()
                .stream()
                .map(AnimalDTO.AnimalResponseDTO::fromEntity)
                .toList();
    }

    // BUSCAR POR MATRICULA
    public AnimalDTO.AnimalResponseDTO buscarPorMatricula(String matricula) {

        Animal animal = repository.findByMatriculaAndAtivoTrue(matricula)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado"));

        return AnimalDTO.AnimalResponseDTO.fromEntity(animal);
    }

    // ATUALIZAR
    public AnimalDTO.AnimalResponseDTO atualizar(String matricula, AnimalDTO.AnimalRegistroDTO dto) {

        Animal animal = repository.findByMatriculaAndAtivoTrue(matricula)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado"));

        animal.setNome(dto.nome());
        animal.setRaca(dto.raca());
        animal.setSexo(dto.sexo());
        animal.setPorte(dto.porte());
        animal.setDataNascimento(dto.dataNascimento());
        animal.setDescricao(dto.descricao());

        repository.save(animal);

        return AnimalDTO.AnimalResponseDTO.fromEntity(animal);
    }

    // SOFT DELETE
    public void deletar(String matricula) {

        Animal animal = repository.findByMatriculaAndAtivoTrue(matricula)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado"));

        animal.setAtivo(false);

        repository.save(animal);
    }

}