package com.senai.nprrs_tagdog_backend.unit.service;

import com.senai.nprrs_tagdog_backend.application.dto.AdminDTO;
import com.senai.nprrs_tagdog_backend.application.service.AdminService;
import com.senai.nprrs_tagdog_backend.domain.entity.Admin;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import com.senai.nprrs_tagdog_backend.domain.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService service;

    private AdminDTO.AdminRegistroDTO dto;

    @BeforeEach
    void setUp() {

        dto = new AdminDTO.AdminRegistroDTO(
                "Sabrina",
                "sabrina@email.com",
                "123456"
        );
    }

    @Test
    @DisplayName("Deve registrar administrador")
    void deveRegistrarAdministrador() {

        when(adminRepository.findByEmail(dto.email()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(dto.senha()))
                .thenReturn("senhaCriptografada");

        when(adminRepository.save(any(Admin.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AdminDTO.AdminResponseDTO resultado =
                service.registrarAdmin(dto);

        assertNotNull(resultado);

        assertEquals("Sabrina", resultado.nome());
        assertEquals("sabrina@email.com", resultado.email());

        ArgumentCaptor<Admin> captor =
                ArgumentCaptor.forClass(Admin.class);

        verify(adminRepository).save(captor.capture());

        Admin adminSalvo = captor.getValue();

        assertEquals("Sabrina", adminSalvo.getNome());
        assertEquals("sabrina@email.com", adminSalvo.getEmail());
        assertEquals("senhaCriptografada", adminSalvo.getSenha());
    }

    @Test
    @DisplayName("Deve listar admins")
    void deveListarAdmins() {

        Admin admin1 = new Admin();
        admin1.setNome("Sabrina");
        admin1.setEmail("sabrina@email.com");
        admin1.setSenha("123456");
        admin1.setRole(Role.ADMIN);

        Admin admin2 = new Admin();
        admin2.setNome("Fabiano");
        admin2.setEmail("fabiano@email.com");
        admin2.setSenha("654321");
        admin2.setRole(Role.ADMIN);

        when(adminRepository.findAll())
                .thenReturn(List.of(admin1, admin2));

        List<AdminDTO.AdminResponseDTO> resultado = service.listarAdmin();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        assertEquals("Sabrina", resultado.get(0).nome());
        assertEquals("sabrina@email.com", resultado.get(0).email());

        assertEquals("Fabiano", resultado.get(1).nome());
        assertEquals("fabiano@email.com", resultado.get(1).email());

        verify(adminRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar admin")
    void deveAtualizarAdministrador() throws Exception {

        String email = "sabrina@email.com";

        AdminDTO.AdminRegistroDTO dto =
                new AdminDTO.AdminRegistroDTO(
                        "Sabrina Atualizada",
                        "sabrina@email.com",
                        "654321"
                );

        AdminDTO.AdminResponseDTO response =
                new AdminDTO.AdminResponseDTO(
                        "Sabrina Atualizada",
                        "sabrina@email.com",
                        true,
                        Role.ADMIN
                );

    }
}