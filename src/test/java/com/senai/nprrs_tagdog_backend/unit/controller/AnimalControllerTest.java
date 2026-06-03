package com.senai.nprrs_tagdog_backend.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.senai.nprrs_tagdog_backend.application.dto.AnimalDTO;
import com.senai.nprrs_tagdog_backend.application.dto.TutorDTO;
import com.senai.nprrs_tagdog_backend.application.service.AnimalService;
import com.senai.nprrs_tagdog_backend.infrastructure.security.JwtService;
import com.senai.nprrs_tagdog_backend.domain.entity.PorteAnimal;
import com.senai.nprrs_tagdog_backend.domain.entity.SexoAnimal;
import com.senai.nprrs_tagdog_backend.domain.entity.Tutor;
import com.senai.nprrs_tagdog_backend.infrastructure.security.UsuarioDetailsService;
import com.senai.nprrs_tagdog_backend.interface_ui.controller.AnimalController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnimalController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AnimalControllerTest {
    //Esses testes foram feitos por Rafael Borges Gibertoni

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AnimalService animalService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UsuarioDetailsService usuarioDetailsService;

    private AnimalDTO.AnimalRegistroDTO registroDTO;
    private AnimalDTO.AnimalResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        // Criando Tutor Mock para o DTO de resposta
        Tutor tutor = new Tutor();
        tutor.setNome("Tutor Teste");
        tutor.setEmail("admin@email.com");
        TutorDTO.TutorResponseDadosPrincipaisDTO tutorDTO = TutorDTO.TutorResponseDadosPrincipaisDTO.fromEntity(tutor);

        // Entrada: AnimalRegistroDTO
        registroDTO = new AnimalDTO.AnimalRegistroDTO(
                "imagem_base64", "Bob", "Golden Retriever", SexoAnimal.MACHO,
                PorteAnimal.GRANDE, LocalDate.of(2026, 2, 19), "Alergia a chocolate", ""
        );

        // Saída: AnimalResponseDTO (seguindo EXATAMENTE a ordem do seu record)
        responseDTO = new AnimalDTO.AnimalResponseDTO(
                "imagem_url",            // imagem
                "TD-12345",              // matricula
                tutorDTO,                // tutor
                "Bob",                   // nome
                "Golden Retriever",      // raca
                SexoAnimal.MACHO,        // sexo
                PorteAnimal.GRANDE,       // porte
                LocalDate.of(2026, 2, 19), // dataNascimento
                "Alergia a chocolate",   // descricao
                "TAG-999",               // numeroTag
                new ArrayList<>(),       // checkInCheckOut
                true                     // ativo
        );
    }

    @Test
    @WithMockUser(username = "admin")
    @DisplayName("POST - Deve criar animal e retornar 201")
    void deveCriarAnimalComSucesso() throws Exception {
        Mockito.when(animalService.registrar(any(), eq("admin@email.com")))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/animais/emailOuCpfTutor/{emailOuCpfTutor}", "admin@email.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/animais/matricula/TD-12345"))
                .andExpect(jsonPath("$.matricula").value("TD-12345"))
                .andExpect(jsonPath("$.nome").value("Bob"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET - Listar animais")
    void deveListarAnimais() throws Exception {
        Mockito.when(animalService.listar()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/animais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matricula").value("TD-12345"))
                .andExpect(jsonPath("$[0].nome").value("Bob"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET - Buscar por matrícula")
    void deveBuscarPorMatricula() throws Exception {
        Mockito.when(animalService.buscarPorMatricula("TD-12345")).thenReturn(responseDTO);

        mockMvc.perform(get("/api/animais/matricula/TD-12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value("TD-12345"));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT - Atualizar animal")
    void deveAtualizarAnimal() throws Exception {
        Mockito.when(animalService.atualizar(eq("TD-12345"), any())).thenReturn(responseDTO);

        mockMvc.perform(put("/api/animais/matricula/TD-12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Bob"));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT - Adicionar Tag")
    void deveAtualizarTag() throws Exception {
        Mockito.when(animalService.tag("TD-12345", "TAG123")).thenReturn(responseDTO);

        mockMvc.perform(put("/api/animais/matricula/TD-12345/tag/TAG123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroTag").value("TAG-999"));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE - Deletar animal")
    void deveDeletarAnimal() throws Exception {
        mockMvc.perform(delete("/api/animais/deletar/matricula/TD-12345"))
                .andExpect(status().isOk()); // Alterado de isNoContent para isOk

        Mockito.verify(animalService).deletar("TD-12345");
    }
}