package com.senai.nprrs_tagdog_backend.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.nprrs_tagdog_backend.application.dto.LocalCoordenadasDTO;
import com.senai.nprrs_tagdog_backend.application.service.LocalCoordenadasService;
import com.senai.nprrs_tagdog_backend.infrastructure.security.JwtAuthenticationFilter;
import com.senai.nprrs_tagdog_backend.infrastructure.security.JwtService;
import com.senai.nprrs_tagdog_backend.interface_ui.controller.LocalCoordenadasController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocalCoordenadasController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LocalCoordenadasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LocalCoordenadasService localCoordenadasService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("Deve cadastrar coordenada retornando status 201")
    void deveCadastrarCoordenada() throws Exception {
        LocalCoordenadasDTO dto = new LocalCoordenadasDTO(
                "04023-001",
                "-23.591348",
                "-46.645165",
                10
        );

        when(localCoordenadasService.registrar(any(LocalCoordenadasDTO.class)))
                .thenReturn(dto);

        mockMvc.perform(post("/api/local")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/local/04023-001")))
                .andExpect(jsonPath("$.cep").value("04023-001"));
    }

    @Test
    @DisplayName("Deve buscar coordenada retornando status 200")
    void deveBuscarCoordenada() throws Exception {
        LocalCoordenadasDTO dto = new LocalCoordenadasDTO("04023-001", "-23.5", "-46.6", 10);

        when(localCoordenadasService.buscar()).thenReturn(dto);

        mockMvc.perform(get("/api/local")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep").value("04023-001"));
    }

    @Test
    @DisplayName("Deve deletar coordenada retornando status 204")
    void deveDeletarCoordenada() throws Exception {
        String cep = "04023-001";

        doNothing().when(localCoordenadasService).deletar(cep);

        mockMvc.perform(delete("/api/local/cep/{cep}", cep)
                        .with(csrf()))
                .andExpect(status().isNoContent()); // Aqui validamos o 204 conforme conversamos
    }
}