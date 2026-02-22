package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.FuncionarioDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Funcionario;
import com.senai.nprrs_tagdog_backend.domain.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FuncionarioService {
    private final FuncionarioRepository funcionarioRepository;
//    private final PasswordEncoder passwordEncoder;

    public FuncionarioDTO.FuncionarioResponseDTO registrarFuncionario(FuncionarioDTO.FuncionarioRegistroDTO dto) {
//        cliente.setSenha(passwordEncoder.encode(dto.senha()));
        return FuncionarioDTO.FuncionarioResponseDTO.fromEntity(funcionarioRepository.save(dto.toEntity()));
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
        return FuncionarioDTO.FuncionarioResponseDTO.fromEntity(buscarFuncionarioPorCpfEAtivoTrue(email));
    }

    public FuncionarioDTO.FuncionarioResponseDTO atualizarFuncionario(String email, FuncionarioDTO.FuncionarioRegistroDTO dto) {
        Funcionario funcionario = buscarFuncionarioPorCpfEAtivoTrue(email);

        funcionario.setNome(dto.nome());
        funcionario.setEmail(dto.email());
        funcionario.setSenha(dto.senha()); //funcionario.setSenha(passwordEncoder.encode(dto.senha()));
        return FuncionarioDTO.FuncionarioResponseDTO.fromEntity(funcionarioRepository.save(funcionario));
    }

    public void desativarFuncionario(String email) {
        Funcionario funcionario = buscarFuncionarioPorCpfEAtivoTrue(email);

        funcionario.setAtivo(false);
        funcionarioRepository.save(funcionario);
    }

    private Funcionario buscarFuncionarioPorCpfEAtivoTrue(String email){
        return funcionarioRepository.findByEmailAndAtivoTrue(email).orElseThrow(
                () -> new RuntimeException()); //EntidadeNaoEncontradaException("Cliente")
    }
}
