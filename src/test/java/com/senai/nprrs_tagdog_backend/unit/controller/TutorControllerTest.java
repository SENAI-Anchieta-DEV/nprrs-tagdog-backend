package com.senai.nprrs_tagdog_backend.unit.controller;

import com.senai.nprrs_tagdog_backend.application.service.TutorService;
import com.senai.nprrs_tagdog_backend.infrastructure.security.JwtService;
import com.senai.nprrs_tagdog_backend.infrastructure.security.UsuarioDetailsService; // Importe isso
import com.senai.nprrs_tagdog_backend.interface_ui.controller.TutorController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TutorController.class)
@AutoConfigureMockMvc(addFilters = false)
class TutorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TutorService tutorService;

    @MockBean
    private JwtService jwtService;

    // CORREÇÃO 1: Mockar o serviço que o filtro de segurança exige
    @MockBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    @DisplayName("Deve listar todos os tutores e retornar 200 OK")
    void deveListarTutores() throws Exception {
        when(tutorService.listarTutores()).thenReturn(List.of());

        // CORREÇÃO 2: Verifique se a rota é /api/tutor ou /api/tutores
        // Como o cadastro foi em /api/tutores, aqui provavelmente também é plural
        mockMvc.perform(get("/api/tutores")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}