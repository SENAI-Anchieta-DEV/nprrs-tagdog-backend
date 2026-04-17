package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.AdminDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Admin;
import com.senai.nprrs_tagdog_backend.domain.exceptions.ConflitosDeEstadoException;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeDuplicadaException;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeNaoEncontradaException;
import com.senai.nprrs_tagdog_backend.domain.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminDTO.AdminResponseDTO registrarAdmin(AdminDTO.AdminRegistroDTO dto) {
        if (adminRepository.findByEmail(dto.email()).isPresent()) {
            throw new EntidadeDuplicadaException("Administrador com este e-mail");
        }
        Admin admin = dto.toEntity();
        admin.setSenha(passwordEncoder.encode(dto.senha()));
        log.info("Cadastrar Admin com email " +  admin.getEmail());
        return AdminDTO.AdminResponseDTO.fromEntity(adminRepository.save(admin));
    }

    @Transactional(readOnly = true)
    public List<AdminDTO.AdminResponseDTO> listarAdmin() {
        log.info("Listar Admin");
        return adminRepository.findAll()
                .stream()
                .map(AdminDTO.AdminResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminDTO.AdminResponseDTO buscarAdminEmail(String email) {
        log.info("Buscar Admin por email " + email);
        return AdminDTO.AdminResponseDTO.fromEntity(buscarAdminPorEmail(email));
    }

    public AdminDTO.AdminResponseDTO atualizarAdmin(String email, AdminDTO.AdminRegistroDTO dto) {
        Admin admin = buscarAdminPorEmail(email);

        admin.setNome(dto.nome());
        admin.setEmail(dto.email());
        admin.setSenha(passwordEncoder.encode(dto.senha()));
        log.info("Atualizar Admin com email " +  admin.getEmail());
        return AdminDTO.AdminResponseDTO.fromEntity(adminRepository.save(admin));
    }

    public void desativarAdmin(String email) {
        Admin admin = buscarAdminPorEmail(email);

        if (admin.isAtivo()){
            admin.setAtivo(false);
            log.info("Desativar Admin com email " + email);
            adminRepository.save(admin);
        } else {
            admin.setAtivo(true);
            log.info("Reativar Admin com email " + email);
            adminRepository.save(admin);
        }

        adminRepository.save(admin);
    }

    private Admin buscarAdminPorEmail(String email) {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Administrador não encontrado"));
    }
}
