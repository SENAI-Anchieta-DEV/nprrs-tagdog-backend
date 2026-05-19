package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.AnimalDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Animal;
import com.senai.nprrs_tagdog_backend.domain.entity.CheckInCheckOut;
import com.senai.nprrs_tagdog_backend.domain.entity.CheckInOuCheckOut;
import com.senai.nprrs_tagdog_backend.domain.entity.Tutor;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeNaoEncontradaException;
import com.senai.nprrs_tagdog_backend.domain.exceptions.RegraNegocioException;
import com.senai.nprrs_tagdog_backend.domain.repository.AnimalRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.CheckInCheckOutRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class AnimalService {

    private final AnimalRepository repository;
    private final TutorRepository tutorRepository;
    private final CheckInCheckOutRepository checkInCheckOutRepository;

    @Transactional
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

        log.info("Cadastrar Animal com matricula " + animal.getMatricula());
        log.info("Adicionar animal cadastrado em Tutor com email ou cpf " + emailOuCpfTutor);
        return AnimalDTO.AnimalResponseDTO.fromEntity(animal, tutor);
    }

    @Transactional
    public List<AnimalDTO.AnimalResponseDTO> listar() {
        log.info("Listar Animais");
        return repository.findAll()
                .stream()
                .map(animal -> {
                    Tutor tutor = tutorRepository.findByAnimais(animal);
                    return AnimalDTO.AnimalResponseDTO.fromEntity(animal, tutor);
                })
                .toList();
    }

    @Transactional
    public List<AnimalDTO.AnimalResponseDTO> listarAnimaisSemFuncionario() {
        log.info("Listar Animais sem Funcionário cuidando");
        return repository.findAnimaisSemFuncionario()
                .stream()
                .map(animal -> {
                    Tutor tutor = tutorRepository.findByAnimais(animal);
                    return AnimalDTO.AnimalResponseDTO.fromEntity(animal, tutor);
                })
                .toList();
    }

    @Transactional
    public AnimalDTO.AnimalResponseDTO buscarPorMatricula(String matricula) {

        Animal animal = repository.findByMatricula(matricula)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Animal"));

        Tutor tutor = tutorRepository.findByAnimais(animal);

        log.info("Buscar Animal por matricula " + matricula);
        return AnimalDTO.AnimalResponseDTO.fromEntity(animal, tutor);
    }

    @Transactional
    public AnimalDTO.AnimalResponseDTO atualizar(String matricula, AnimalDTO.AnimalRegistroDTO dto) {

        Animal animal = repository.findByMatricula(matricula)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado"));
        Tutor tutor = tutorRepository.findByAnimais(animal);

        if(!animal.isAtivo()){
            throw new RegraNegocioException("Entidade inativa");
        }
        animal.setImagem(dto.imagem());
        animal.setNome(dto.nome());
        animal.setRaca(dto.raca());
        animal.setSexo(dto.sexo());
        animal.setPorte(dto.porte());
        animal.setDataNascimento(dto.dataNascimento());
        animal.setDescricao(dto.descricao());

        repository.save(animal);

        log.info("Atualizar Animal com matricula " + animal.getMatricula());
        return AnimalDTO.AnimalResponseDTO.fromEntity(animal, tutor);
    }

    @Transactional
    public AnimalDTO.AnimalResponseDTO tag(String matricula, String tag) {

        Animal animal = repository.findByMatricula(matricula)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado"));
        Tutor tutor = tutorRepository.findByAnimais(animal);

        if(!animal.isAtivo()){
            throw new RegraNegocioException("Entidade inativa");
        }
        animal.setNumeroTag(tag);
        repository.save(animal);

        log.info("Adicionar Tag " + tag + " no Animal com matricula " + matricula);
        return AnimalDTO.AnimalResponseDTO.fromEntity(animal, tutor);
    }

    @Transactional
    public AnimalDTO.AnimalResponseDTO checkInOuCheckOut(String matricula) {

        Animal animal = repository.findByMatricula(matricula)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado"));

        Tutor tutor = tutorRepository.findByAnimais(animal);

        if(!animal.isAtivo()){
            throw new RegraNegocioException("Entidade inativa");
        }
        CheckInCheckOut entity = new CheckInCheckOut();
        if(!animal.getCheckInCheckOut().isEmpty() && animal.getCheckInCheckOut().getLast().getCheckInOuCheckOut() == CheckInOuCheckOut.CHECK_IN){
            entity.setCheckInOuCheckOut(CheckInOuCheckOut.CHECK_OUT);
        } else {
            entity.setCheckInOuCheckOut(CheckInOuCheckOut.CHECK_IN);
        }

        entity.setDataHora(LocalDateTime.now());
        checkInCheckOutRepository.save(entity);

        animal.getCheckInCheckOut().add(entity);
        repository.save(animal);

        return AnimalDTO.AnimalResponseDTO.fromEntity(animal, tutor);
    }

    public void deletar(String matricula) {

        Animal animal = repository.findByMatricula(matricula)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Animal não encontrado"));

        if (animal.isAtivo()){
            animal.setAtivo(false);
            log.info("Desativar Animal com matricula " + matricula);
            repository.save(animal);
        } else {
            if(tutorRepository.findByAnimais(animal).isAtivo()){
                animal.setAtivo(true);
                log.info("Reativar Animal com matricula " + matricula);
                repository.save(animal);
            }
        }

        repository.save(animal);
    }

}