package com.senai.nprrs_tagdog_backend.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.nprrs_tagdog_backend.application.dto.AnimalDTO;
import com.senai.nprrs_tagdog_backend.application.dto.TagDTO;
import com.senai.nprrs_tagdog_backend.application.service.TagService;
import com.senai.nprrs_tagdog_backend.domain.entity.PorteAnimal;
import com.senai.nprrs_tagdog_backend.domain.entity.SexoAnimal;
import com.senai.nprrs_tagdog_backend.infrastructure.security.JwtService;
import com.senai.nprrs_tagdog_backend.infrastructure.security.UsuarioDetailsService;
import com.senai.nprrs_tagdog_backend.interface_ui.controller.TagController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
@AutoConfigureMockMvc(addFilters = false)
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TagService tagService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UsuarioDetailsService userDetailsService;

    @Test
    @DisplayName("Deve retornar lista de posições atuais das tags")
    void deveRetornarPosicoesAtuais() throws Exception {

        AnimalDTO.AnimalResponseSemTutorDTO animalDTO = new AnimalDTO.AnimalResponseSemTutorDTO("","TD-12345", "Bob", "Vira-lata", SexoAnimal.MACHO, PorteAnimal.MEDIO, LocalDate.now(),"Descrição","",new ArrayList<>(),true);

        TagDTO.TagResponseDTO tag1 = new TagDTO.TagResponseDTO("mac1", "-23.550520", "-46.633308", animalDTO, LocalDateTime.now(),true, true);
        TagDTO.TagResponseDTO tag2 = new TagDTO.TagResponseDTO("mac2", "-23.550520", "-46.633308", animalDTO, LocalDateTime.now(), true, true);

        when(tagService.buscarPosicoesAtuais()).thenReturn(List.of(tag1, tag2));

        mockMvc.perform(get("/api/tags/posicoes-atuais")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numero").value("mac1"))
                .andExpect(jsonPath("$[0].animal.nome").value("Bob"))
                .andExpect(jsonPath("$[1].numero").value("mac2"))
                .andExpect(jsonPath("$[1].animal.nome").value("Bob"));

        verify(tagService, times(1)).buscarPosicoesAtuais();
    }

    @Test
    @DisplayName("Deve delegar salvar tag ao serviço diretamente")
    void deveDelegarSalvarTag() {

        TagDTO.TagRegistroDTO dto = new TagDTO.TagRegistroDTO("TAG-003", "-23.550520", "-46.633308", "25/03/2026 11:00:00");

        TagController controller = new TagController(tagService);
        controller.salvar(dto);

        verify(tagService, times(1)).salvar(dto);
    }
}