package com.senai.nprrs_tagdog_backend.infrastructure.config;

import com.senai.nprrs_tagdog_backend.domain.entity.Admin;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import com.senai.nprrs_tagdog_backend.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${sistema.admin.email}")
    private String adminEmail;

    @Value("${sistema.admin.senha}")
    private String adminSenha;

    @Override
    public void run(String... args) {
        usuarioRepository.findByEmail(adminEmail).ifPresentOrElse(
                usuario -> {
                    if (!usuario.isAtivo()) {
                        usuario.setAtivo(true);
                        usuarioRepository.save(usuario);
                    }
                },
                () -> {
                    Admin admin = Admin.builder()
                            .nome("Administrador Provisório")
                            .email(adminEmail)
                            .senha(passwordEncoder.encode(adminSenha))
                            .role(Role.ADMIN)
                            .ativo(true)
                            .build();
                    usuarioRepository.save(admin);
                    System.out.println("Usuário admin provisório criado: " + adminEmail);
                }
        );
    }
}