package com.senai.nprrs_tagdog_backend.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.nprrs_tagdog_backend.application.dto.LocalCoordenadasDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.LocalCoordenadas;
import com.senai.nprrs_tagdog_backend.domain.repository.LocalCoordenadasRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

// IMPORTS ESTÁTICOS CORRETOS DO MOCKMVC
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class LocalCoordenadasControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LocalCoordenadasRepository localCoordenadasRepository;

    @BeforeEach
    void setUp() {
        localCoordenadasRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve cadastrar local coordenadas e retornar 201")
    void deveCadastrarLocalComSucesso() throws Exception {
        LocalCoordenadasDTO dto = new LocalCoordenadasDTO(
                "04023-001",
                "-23.591348",
                "-46.645165",
                10
        );

        mockMvc.perform(post("/api/local")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cep").value("04023-001"))
                .andExpect(header().string("Location", containsString("/api/local/04023-001")));

        Assertions.assertEquals(1, localCoordenadasRepository.count());
    }

    @Test
    @DisplayName("Deve buscar o local cadastrado e retornar 200")
    void deveBuscarLocalComSucesso() throws Exception {
        LocalCoordenadas local = LocalCoordenadas.builder()
                .cep("12345-678")
                .latitude("-10.000")
                .longitude("-20.000")
                .raio(50)
                .build();
        localCoordenadasRepository.save(local);

        mockMvc.perform(get("/api/local")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep").value("12345-678"));
    }

    @Test
    @DisplayName("Deve deletar o local pelo CEP e retornar 204")
    void deveDeletarLocalComSucesso() throws Exception {
        String cepParaDeletar = "99999-999";
        LocalCoordenadas local = LocalCoordenadas.builder()
                .cep(cepParaDeletar)
                .latitude("0.0")
                .longitude("0.0")
                .raio(5)
                .build();
        localCoordenadasRepository.save(local);

        mockMvc.perform(delete("/api/local/cep/{cep}", cepParaDeletar))
                .andExpect(status().isNoContent());

        Assertions.assertEquals(0, localCoordenadasRepository.count());
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar deletar local inexistente")
    void deveRetornar404AoDeletarInexistente() throws Exception {
        mockMvc.perform(delete("/api/local/cep/00000-000"))
                .andExpect(status().isNotFound());
    }
}