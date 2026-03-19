package com.senai.nprrs_tagdog_backend.infrastructure.config;

import com.senai.nprrs_tagdog_backend.domain.entity.Admin;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import com.senai.nprrs_tagdog_backend.domain.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${sistema.admin.email}")
    private String adminEmail;

    @Value("${sistema.admin.senha}")
    private String adminSenha;

    @Override
    public void run(String... args) {
        // Verifica se já existe um admin com o email padrão
        if (adminRepository.findByEmailAndAtivoTrue(adminEmail).isEmpty()) {
            Admin admin = Admin.builder()
                    .nome("Administrador Sistema")
                    .email(adminEmail)
                    .senha(passwordEncoder.encode(adminSenha))
                    .ativo(true)
                    .role(Role.ADMIN)
                    .build();
            adminRepository.save(admin);
            System.out.println("✅ Admin padrão criado com sucesso!");
            System.out.println("   Email: " + adminEmail);
            System.out.println("   Senha: " + adminSenha);
        } else {
            System.out.println("✅ Admin padrão já existe no sistema.");
        }
    }
}


