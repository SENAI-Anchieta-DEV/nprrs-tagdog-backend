package com.senai.nprrs_tagdog_backend.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.nprrs_tagdog_backend.application.dto.TagDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.*;
import com.senai.nprrs_tagdog_backend.domain.repository.AnimalRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.LocalCoordenadasRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.TagRepository;
import com.senai.nprrs_tagdog_backend.infrastructure.security.JwtService;
import com.senai.nprrs_tagdog_backend.interface_ui.controller.TagController;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TagControllerIntegrationTest {
    //Esses testes foram feitos por Raquel Yukie Tsuji

    @LocalServerPort
    private int port;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private LocalCoordenadasRepository localCoordenadasRepository;

    private RequestSpecification requestSpecification;

    @Autowired
    private TagController tagController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private static final String ADMIN_EMAIL = "admin@email.com";
    private static final String MATRICULA_ANIMAL = "TD-12345";

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();

        limparBanco();
    }

    private void limparBanco() {
        localCoordenadasRepository.deleteAll();
        tagRepository.deleteAll();
        animalRepository.deleteAll();
    }

    private String gerarTokenAdmin() {
        return jwtService.generateToken(ADMIN_EMAIL, "ADMIN");
    }

    private RequestSpecification requestComAuth(String token) {
        return given()
                .spec(requestSpecification)
                .header("Authorization", "Bearer " + token);
    }

    private Animal criarAnimal() {
        Animal animal = new Animal();
        animal.setMatricula(MATRICULA_ANIMAL);
        animal.setNome("Nome Animal");
        animal.setRaca("Nome Raca");
        animal.setSexo(SexoAnimal.FEMEA);
        animal.setPorte(PorteAnimal.MEDIO);
        animal.setDataNascimento(LocalDate.parse("2026-05-08"));
        animal.setDescricao("Descricao");
        animal.setAtivo(true);
        animal.setCheckInCheckOut(new ArrayList<>());
        animal.setNumeroTag("mac");

        return animalRepository.save(animal);
    }

    private Tag criarTag() {
        Animal animal = animalRepository.findByMatricula(MATRICULA_ANIMAL)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado"));

        Tag tag = new Tag();
        tag.setNumero(animal.getNumeroTag());
        tag.setLatitude(String.valueOf(-23.550520));
        tag.setLongitude(String.valueOf(-46.633308));
        tag.setDataCriado(LocalDateTime.now());
        tag.setAnimal(animal);

        return tagRepository.save(tag);
    }

    private LocalCoordenadas criarLocal() {
        LocalCoordenadas local = new LocalCoordenadas();
        local.setCep("04023-001");
        local.setLatitude(String.valueOf(-23.550520));
        local.setLongitude(String.valueOf(-46.633308));
        local.setRaio(4);

        return localCoordenadasRepository.save(local);
    }

    @Test
    @DisplayName("Deve buscar posições atuais com sucesso")
    void deveBuscarPosicoesAtuais() {
        String token = gerarTokenAdmin();

        criarLocal();
        Animal animal = criarAnimal();
        criarTag();

        requestComAuth(token)
                .when()
                .get("/api/tags/posicoes-atuais")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].latitude", notNullValue())
                .body("[0].longitude", notNullValue())
                .body("[0].animal.matricula", equalTo(animal.getMatricula()))
                .body("[0].animal.nome", equalTo(animal.getNome()));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver tags")
    void deveRetornarListaVaziaQuandoNaoHouverTags() {
        String token = gerarTokenAdmin();

        requestComAuth(token)
                .when()
                .get("/api/tags/posicoes-atuais")
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));
    }

    @Test
    @DisplayName("Deve salvar tag recebida via MQTT com sucesso")
    void deveSalvarTagViaMqtt() {
        criarLocal();
        Animal animal = criarAnimal();

        TagDTO.TagRegistroDTO dto = new TagDTO.TagRegistroDTO(
                animal.getNumeroTag(),
                String.valueOf(-23.550520),
                String.valueOf(-46.633308),
                "25/03/2026 11:00:00"
        );

        tagController.salvar(dto);

        List<Tag> tags = tagRepository.findAll();
        assertEquals(1, tags.size());

        Tag tag = tags.get(0); // List não tem getFirst()
        assertEquals(String.valueOf(-23.550520), tag.getLatitude());
        assertEquals(String.valueOf(-46.633308), tag.getLongitude());
        assertEquals(animal.getMatricula(), tag.getAnimal().getMatricula());
    }
}
