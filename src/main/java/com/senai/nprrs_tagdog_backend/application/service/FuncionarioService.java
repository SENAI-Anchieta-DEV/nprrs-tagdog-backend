package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.FuncionarioDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Animal;
import com.senai.nprrs_tagdog_backend.domain.entity.Funcionario;
import com.senai.nprrs_tagdog_backend.domain.exceptions.ConflitosDeEstadoException;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeDuplicadaException;
import com.senai.nprrs_tagdog_backend.domain.repository.AnimalRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeNaoEncontradaException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class FuncionarioService {
    private final FuncionarioRepository funcionarioRepository;
    private final AnimalRepository animalRepository;
    private final PasswordEncoder passwordEncoder;

    public FuncionarioDTO.FuncionarioResponseDTO registrarFuncionario(FuncionarioDTO.FuncionarioRegistroDTO dto) {
        if (funcionarioRepository.findByEmail(dto.email()).isPresent()) { //fiz um metodo no Repository funcionario
            throw new EntidadeDuplicadaException("Funcionário com este email");
        }
        Funcionario funcionario = dto.toEntity();
        funcionario.setSenha(passwordEncoder.encode(dto.senha()));
        log.info("Cadastrar Funcionario com email " +  funcionario.getEmail());
        return FuncionarioDTO.FuncionarioResponseDTO.fromEntity(funcionarioRepository.save(funcionario));
    }

    public FuncionarioDTO.FuncionarioResponseDTO adicionarAnimalNoFuncionario(String email, String matriculaAnimal) {
        Funcionario funcionario = buscarFuncionarioPorEmail(email);
        Animal animal = animalRepository.findByMatricula(matriculaAnimal).orElseThrow(
                () -> new EntidadeNaoEncontradaException("Animal"));
        if(funcionario.getAnimais().contains(animal)){
            funcionario.getAnimais().remove(animal);
        } else {
            funcionario.getAnimais().add(animal);
        }

        log.info("Adicionar Animal com matricula " + animal.getMatricula() + " sob os cuidados do Funcionario com email " +  funcionario.getEmail());
        return FuncionarioDTO.FuncionarioResponseDTO.fromEntity(funcionarioRepository.save(funcionario));
    }

    @Transactional(readOnly = true)
    public List<FuncionarioDTO.FuncionarioResponseDTO> listarFuncionarios() {
        log.info("Listar Admin");
        return funcionarioRepository.findAll()
                .stream()
                .map(FuncionarioDTO.FuncionarioResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public FuncionarioDTO.FuncionarioResponseDTO buscarFuncionarioEmail(String email) {
        log.info("Buscar Funcionario por email " + email);
        return FuncionarioDTO.FuncionarioResponseDTO.fromEntity(buscarFuncionarioPorEmail(email));
    }

    public FuncionarioDTO.FuncionarioResponseDTO atualizarFuncionario(String email, FuncionarioDTO.FuncionarioAtualizarDTO dto) {
        Funcionario funcionario = buscarFuncionarioPorEmail(email);

        funcionario.setNome(dto.nome());
        funcionario.setEmail(dto.email());
        if (dto.senha() != null && !dto.senha().isBlank()) {
            funcionario.setSenha(passwordEncoder.encode(dto.senha()));
        }
        log.info("Atualizar Funcionario com email " +  funcionario.getEmail());
        return FuncionarioDTO.FuncionarioResponseDTO.fromEntity(funcionarioRepository.save(funcionario));
    }

    public void desativarFuncionario(String email) {
        Funcionario funcionario = buscarFuncionarioPorEmail(email);

        if (funcionario.isAtivo()){
            funcionario.setAtivo(false);
            log.info("Desativar Funcionario com email " + email);
            funcionarioRepository.save(funcionario);
        } else {
            funcionario.setAtivo(true);
            log.info("Reativar Funcionario com email " + email);
            funcionarioRepository.save(funcionario);
        }

        funcionarioRepository.save(funcionario);
    }

    private Funcionario buscarFuncionarioPorEmail(String email){
        return funcionarioRepository.findByEmail(email).orElseThrow(
                () -> new EntidadeNaoEncontradaException("Funcionário"));
    }
}
