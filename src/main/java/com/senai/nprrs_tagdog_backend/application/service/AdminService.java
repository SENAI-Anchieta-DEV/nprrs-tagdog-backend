package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.AdminDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Admin;
import com.senai.nprrs_tagdog_backend.domain.exceptions.ConflitosDeEstadoException;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeDuplicadaException;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeNaoEncontradaException;
import com.senai.nprrs_tagdog_backend.domain.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminDTO.AdminResponseDTO registrarAdmin(AdminDTO.AdminRegistroDTO dto) {
        if (adminRepository.findByEmail(dto.email()).isPresent()) {
            throw new EntidadeDuplicadaException("Administrador com este e-mail");
        }
        Admin admin = dto.toEntity();
        admin.setSenha(passwordEncoder.encode(dto.senha()));
        return AdminDTO.AdminResponseDTO.fromEntity(adminRepository.save(admin));
    }

    @Transactional(readOnly = true)
    public List<AdminDTO.AdminResponseDTO> listarAdmiAtivos() {
        return adminRepository.findAll()
                .stream()
                .map(AdminDTO.AdminResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminDTO.AdminResponseDTO buscarAdminAtivoPorEmail(String email) {
        return AdminDTO.AdminResponseDTO.fromEntity(buscarAdminPorEmailEAtivoTrue(email));
    }

    public AdminDTO.AdminResponseDTO atualizarAdmin(String email, AdminDTO.AdminRegistroDTO dto) {
        Admin admin = buscarAdminPorEmailEAtivoTrue(email);

        admin.setNome(dto.nome());
        admin.setEmail(dto.email());
        admin.setSenha(passwordEncoder.encode(dto.senha()));
        return AdminDTO.AdminResponseDTO.fromEntity(adminRepository.save(admin));
    }

    public void desativarAdmin(String email) {
        Admin admin = buscarAdminPorEmailEAtivoTrue(email);

        if (!admin.isAtivo()) {
            throw new ConflitosDeEstadoException("Administrador já está desativado.");
        }
        admin.setAtivo(false);
        adminRepository.save(admin);
    }

    private Admin buscarAdminPorEmailEAtivoTrue(String email) {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Administrador não encontrado"));
    }
}
