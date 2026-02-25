package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.FuncionarioDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Funcionario;
import com.senai.nprrs_tagdog_backend.domain.exceptions.ConflitosDeEstadoException;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeDuplicadaException;
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
    private final PasswordEncoder passwordEncoder;

    public FuncionarioDTO.FuncionarioResponseDTO registrarFuncionario(FuncionarioDTO.FuncionarioRegistroDTO dto) {
        if (funcionarioRepository.findByEmailAndAtivoTrue(dto.email()) != null) { //fiz um metodo no Repository funcionario
            throw new EntidadeDuplicadaException("Funcionário com este email");
        }
        Funcionario funcionario = dto.toEntity();
        funcionario.setSenha(passwordEncoder.encode(dto.senha()));
        return FuncionarioDTO.FuncionarioResponseDTO.fromEntity(funcionarioRepository.save(funcionario));
    }

    @Transactional(readOnly = true)
    public List<FuncionarioDTO.FuncionarioResponseDTO> listarFuncionariosAtivos() {
        return funcionarioRepository.findAllByAtivoTrue()
                .stream()
                .map(FuncionarioDTO.FuncionarioResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public FuncionarioDTO.FuncionarioResponseDTO buscarFuncionarioAtivoPorEmail(String email) {
        return FuncionarioDTO.FuncionarioResponseDTO.fromEntity(buscarFuncionarioPorEmailEAtivoTrue(email));
    }

    public FuncionarioDTO.FuncionarioResponseDTO atualizarFuncionario(String email, FuncionarioDTO.FuncionarioRegistroDTO dto) {
        Funcionario funcionario = buscarFuncionarioPorEmailEAtivoTrue(email);

        funcionario.setNome(dto.nome());
        funcionario.setEmail(dto.email());
        funcionario.setSenha(passwordEncoder.encode(dto.senha()));
        return FuncionarioDTO.FuncionarioResponseDTO.fromEntity(funcionarioRepository.save(funcionario));
    }

    public void desativarFuncionario(String email) {
        Funcionario funcionario = buscarFuncionarioPorEmailEAtivoTrue(email);

        if (!funcionario.isAtivo()) {
            throw new ConflitosDeEstadoException("Funcionário já está desativado.");
        }
        funcionario.setAtivo(false);
        funcionarioRepository.save(funcionario);
    }

    private Funcionario buscarFuncionarioPorEmailEAtivoTrue(String email){
        return funcionarioRepository.findByEmailAndAtivoTrue(email).orElseThrow(
                () -> new EntidadeNaoEncontradaException("Funcionário"));
    }
}
