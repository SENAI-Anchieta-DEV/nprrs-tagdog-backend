package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.FuncionarioDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Animal;
import com.senai.nprrs_tagdog_backend.domain.entity.Funcionario;
import com.senai.nprrs_tagdog_backend.domain.exceptions.ConflitosDeEstadoException;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeDuplicadaException;
import com.senai.nprrs_tagdog_backend.domain.repository.AnimalRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeNaoEncontradaException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
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

        return FuncionarioDTO.FuncionarioResponseDTO.fromEntity(funcionarioRepository.save(funcionario));
    }

    @Transactional(readOnly = true)
    public List<FuncionarioDTO.FuncionarioResponseDTO> listarFuncionariosAtivos() {
        return funcionarioRepository.findAll()
                .stream()
                .map(FuncionarioDTO.FuncionarioResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public FuncionarioDTO.FuncionarioResponseDTO buscarFuncionarioAtivoPorEmail(String email) {
        return FuncionarioDTO.FuncionarioResponseDTO.fromEntity(buscarFuncionarioPorEmail(email));
    }

    public FuncionarioDTO.FuncionarioResponseDTO atualizarFuncionario(String email, FuncionarioDTO.FuncionarioRegistroDTO dto) {
        Funcionario funcionario = buscarFuncionarioPorEmail(email);

        funcionario.setNome(dto.nome());
        funcionario.setEmail(dto.email());
        funcionario.setSenha(passwordEncoder.encode(dto.senha()));
        return FuncionarioDTO.FuncionarioResponseDTO.fromEntity(funcionarioRepository.save(funcionario));
    }

    public void desativarFuncionario(String email) {
        Funcionario funcionario = buscarFuncionarioPorEmail(email);

        if (!funcionario.isAtivo()) {
            throw new ConflitosDeEstadoException("Funcionário já está desativado.");
        }
        funcionario.setAtivo(false);
        funcionarioRepository.save(funcionario);
    }

    private Funcionario buscarFuncionarioPorEmail(String email){
        return funcionarioRepository.findByEmail(email).orElseThrow(
                () -> new EntidadeNaoEncontradaException("Funcionário"));
    }
}
