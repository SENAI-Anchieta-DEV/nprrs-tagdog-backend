package com.senai.nprrs_tagdog_backend.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.nprrs_tagdog_backend.application.dto.AdminDTO;
import com.senai.nprrs_tagdog_backend.application.service.AdminService;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import com.senai.nprrs_tagdog_backend.infrastructure.security.JwtAuthenticationFilter;
import com.senai.nprrs_tagdog_backend.infrastructure.security.JwtService;
import com.senai.nprrs_tagdog_backend.interface_ui.controller.AdminController;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("Deve cadastrar administrador")
    void deveCadastrarAdministrador() throws Exception {

        AdminDTO.AdminRegistroDTO dto =
                new AdminDTO.AdminRegistroDTO(
                        "Sabrina",
                        "sabrina@email.com",
                        "123456"
                );

        AdminDTO.AdminResponseDTO response =
                new AdminDTO.AdminResponseDTO(
                        "Sabrina",
                        "sabrina@email.com",
                        true,
                        Role.ADMIN
                );

        when(adminService.registrarAdmin(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/admin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Sabrina"))
                .andExpect(jsonPath("$.email").value("sabrina@email.com"));
    }
}