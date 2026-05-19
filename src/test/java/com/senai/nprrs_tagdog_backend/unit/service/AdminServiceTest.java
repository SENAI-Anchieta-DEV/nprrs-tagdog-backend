package com.senai.nprrs_tagdog_backend.unit.service;

import com.senai.nprrs_tagdog_backend.application.dto.AdminDTO;
import com.senai.nprrs_tagdog_backend.application.service.AdminService;
import com.senai.nprrs_tagdog_backend.domain.entity.Admin;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeDuplicadaException;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeNaoEncontradaException;
import com.senai.nprrs_tagdog_backend.domain.repository.AdminRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

class AdminServiceTest {
    //Devido a um erro de merge, o nome do autor foi sobrescrito. Esses testes foram feitos por Sabrina Matos Almeida

    @Mock
    private AdminRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve cadastrar administrador")
    void deveCadastrarAdministrador() {

        AdminDTO.AdminRegistroDTO dto =
                new AdminDTO.AdminRegistroDTO(
                        "Sabrina",
                        "sabrina@email.com",
                        "123456"
                );

        when(repository.findByEmail(dto.email()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(any()))
                .thenReturn("senha");

        Admin admin = dto.toEntity();

        when(repository.save(any(Admin.class)))
                .thenReturn(admin);

        AdminDTO.AdminResponseDTO response =
                service.registrarAdmin(dto);

        assertEquals("Sabrina", response.nome());

        verify(repository, times(1))
                .save(any(Admin.class));
    }

    @Test
    @DisplayName("Não deve cadastrar administrador duplicado")
    void naoDeveCadastrarAdministradorDuplicado() {

        Admin admin = new Admin();
        admin.setEmail("sabrina@email.com");

        when(repository.findByEmail(any()))
                .thenReturn(Optional.of(admin));

        AdminDTO.AdminRegistroDTO dto =
                new AdminDTO.AdminRegistroDTO(
                        "Sabrina",
                        "sabrina@email.com",
                        "123456"
                );

        assertThrows(
                EntidadeDuplicadaException.class,
                () -> service.registrarAdmin(dto)
        );
    }

    @Test
    @DisplayName("Deve listar administradores")
    void deveListarAdministradores() {

        Admin admin = new Admin();

        admin.setNome("Sabrina");
        admin.setEmail("sabrina@email.com");
        admin.setRole(Role.ADMIN);

        when(repository.findAll())
                .thenReturn(List.of(admin));

        List<AdminDTO.AdminResponseDTO> lista =
                service.listarAdmin();

        assertEquals(1, lista.size());
    }

    @Test
    @DisplayName("Deve buscar administrador por email")
    void deveBuscarAdministradorPorEmail() {

        Admin admin = new Admin();

        admin.setNome("Sabrina");
        admin.setEmail("sabrina@email.com");

        when(repository.findByEmail(any()))
                .thenReturn(Optional.of(admin));

        AdminDTO.AdminResponseDTO response =
                service.buscarAdminEmail("sabrina@email.com");

        assertEquals(
                "sabrina@email.com",
                response.email()
        );
    }

    @Test
    @DisplayName("Deve atualizar administrador")
    void deveAtualizarAdministrador() {

        Admin admin = new Admin();

        admin.setNome("Antigo");
        admin.setEmail("sabrina@email.com");

        when(repository.findByEmail(any()))
                .thenReturn(Optional.of(admin));

        when(passwordEncoder.encode(any()))
                .thenReturn("senha");

        when(repository.save(any()))
                .thenReturn(admin);

        AdminDTO.AdminRegistroDTO dto =
                new AdminDTO.AdminRegistroDTO(
                        "Novo Nome",
                        "novo@email.com",
                        "123456"
                );

        AdminDTO.AdminResponseDTO response =
                service.atualizarAdmin(
                        "sabrina@email.com",
                        dto
                );

        assertEquals("Novo Nome", response.nome());
    }

    @Test
    @DisplayName("Deve desativar administrador")
    void deveDesativarAdministrador() {

        Admin admin = new Admin();

        admin.setAtivo(true);
        admin.setEmail("sabrina@email.com");

        when(repository.findByEmail(any()))
                .thenReturn(Optional.of(admin));

        service.desativarAdmin("sabrina@email.com");

        assertFalse(admin.isAtivo());

        verify(repository, atLeastOnce())
                .save(admin);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar admin inexistente")
    void deveLancarExcecaoAoBuscarAdminInexistente() {

        when(repository.findByEmail(any()))
                .thenReturn(Optional.empty());

        assertThrows(
                EntidadeNaoEncontradaException.class,
                () -> service.buscarAdminEmail("teste@email.com")
        );
    }
}