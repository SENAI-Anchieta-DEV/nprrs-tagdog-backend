package com.senai.nprrs_tagdog_backend.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.nprrs_tagdog_backend.application.dto.FuncionarioDTO;
import com.senai.nprrs_tagdog_backend.application.service.FuncionarioService;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import com.senai.nprrs_tagdog_backend.infrastructure.security.JwtService;
import com.senai.nprrs_tagdog_backend.infrastructure.security.UsuarioDetailsService;
import com.senai.nprrs_tagdog_backend.interface_ui.controller.FuncionarioController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FuncionarioController.class)
class FuncionarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FuncionarioService funcionarioService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UsuarioDetailsService userDetailsService;

    @Test
    @DisplayName("Deve delegar cadastro de funcionário ao serviço")
    @WithMockUser
    void deveDelegarCadastroAoServico() throws Exception {

        FuncionarioDTO.FuncionarioRegistroDTO dto =
                new FuncionarioDTO.FuncionarioRegistroDTO(
                        "João",
                        "joao@email.com",
                        "123456"
                );

        FuncionarioDTO.FuncionarioResponseDTO responseEsperada =
                new FuncionarioDTO.FuncionarioResponseDTO(
                        "João",
                        "joao@email.com",
                        new ArrayList<>(),
                        true,
                        Role.FUNCIONARIO
                );

        when(funcionarioService.registrarFuncionario(any()))
                .thenReturn(responseEsperada);

        mockMvc.perform(post("/api/funcionarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));

        verify(funcionarioService, times(1))
                .registrarFuncionario(any());
    }

    @Test
    @DisplayName("Deve delegar busca por email ao serviço")
    @WithMockUser
    void deveDelegarBuscaPorEmailAoServico() throws Exception {

        FuncionarioDTO.FuncionarioResponseDTO responseEsperada =
                new FuncionarioDTO.FuncionarioResponseDTO(
                        "Maria",
                        "maria@email.com",
                        new ArrayList<>(),
                        true,
                        Role.FUNCIONARIO
                );

        when(funcionarioService.buscarFuncionarioEmail("maria@email.com"))
                .thenReturn(responseEsperada);

        mockMvc.perform(get("/api/funcionarios/email/maria@email.com")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.email").value("maria@email.com"));

        verify(funcionarioService, times(1))
                .buscarFuncionarioEmail("maria@email.com");
    }

    @Test
    @DisplayName("Deve delegar listagem de funcionários ao serviço")
    @WithMockUser
    void deveDelegarListagemAoServico() throws Exception {

        List<FuncionarioDTO.FuncionarioResponseDTO> lista =
                List.of(
                        new FuncionarioDTO.FuncionarioResponseDTO(
                                "Carlos",
                                "carlos@email.com",
                                new ArrayList<>(),
                                true,
                                Role.FUNCIONARIO
                        ),
                        new FuncionarioDTO.FuncionarioResponseDTO(
                                "Fernanda",
                                "fernanda@email.com",
                                new ArrayList<>(),
                                true,
                                Role.FUNCIONARIO
                        )
                );

        when(funcionarioService.listarFuncionarios())
                .thenReturn(lista);

        mockMvc.perform(get("/api/funcionarios")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Carlos"))
                .andExpect(jsonPath("$[1].nome").value("Fernanda"));

        verify(funcionarioService, times(1))
                .listarFuncionarios();
    }

    @Test
    @DisplayName("Deve delegar atualização de funcionário ao serviço")
    @WithMockUser
    void deveDelegarAtualizacaoAoServico() throws Exception {

        FuncionarioDTO.FuncionarioAtualizarDTO dto =
                new FuncionarioDTO.FuncionarioAtualizarDTO(
                        "Novo Nome",
                        "novo@email.com",
                        ""
                );

        FuncionarioDTO.FuncionarioResponseDTO responseEsperada =
                new FuncionarioDTO.FuncionarioResponseDTO(
                        "Novo Nome",
                        "novo@email.com",
                        new ArrayList<>(),
                        true,
                        Role.FUNCIONARIO
                );

        when(funcionarioService.atualizarFuncionario(
                eq("funcionario@email.com"),
                any()
        )).thenReturn(responseEsperada);

        mockMvc.perform(put("/api/funcionarios/email/funcionario@email.com")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Novo Nome"));

        verify(funcionarioService, times(1))
                .atualizarFuncionario(eq("funcionario@email.com"), any());
    }

    @Test
    @DisplayName("Deve delegar desativação de funcionário ao serviço")
    @WithMockUser
    void deveDelegarDesativacaoAoServico() throws Exception {

        mockMvc.perform(delete("/api/funcionarios/email/funcionario@email.com")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(funcionarioService, times(1))
                .desativarFuncionario("funcionario@email.com");
    }

    @Test
    @DisplayName("Deve delegar adição de animal ao funcionário")
    @WithMockUser
    void deveDelegarAdicaoAnimalAoServico() throws Exception {

        FuncionarioDTO.FuncionarioResponseDTO responseEsperada =
                new FuncionarioDTO.FuncionarioResponseDTO(
                        "Carlos",
                        "carlos@email.com",
                        new ArrayList<>(),
                        true,
                        Role.FUNCIONARIO
                );

        when(funcionarioService.adicionarAnimalNoFuncionario(
                "carlos@email.com",
                "TD-12345"
        )).thenReturn(responseEsperada);

        mockMvc.perform(post("/api/funcionarios/email/carlos@email.com/animalMatricula/TD-12345")
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Carlos"));

        verify(funcionarioService, times(1))
                .adicionarAnimalNoFuncionario(
                        "carlos@email.com",
                        "TD-12345"
                );
    }

    @Test
    @DisplayName("Deve delegar retirada de animal do funcionário")
    @WithMockUser
    void deveDelegarRetiradaAnimalAoServico() throws Exception {

        mockMvc.perform(put("/api/funcionarios/email/funcionario@email.com/matriculaAnimal/TD-12345")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(funcionarioService, times(1))
                .retirarAnimalDeFuncionario(
                        "funcionario@email.com",
                        "TD-12345"
                );
    }
}