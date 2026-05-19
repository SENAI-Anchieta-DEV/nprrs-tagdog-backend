package com.senai.nprrs_tagdog_backend.integration.controller;

import com.senai.nprrs_tagdog_backend.application.dto.AnimalDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.PorteAnimal;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import com.senai.nprrs_tagdog_backend.domain.entity.SexoAnimal;
import com.senai.nprrs_tagdog_backend.domain.entity.Tutor;
import com.senai.nprrs_tagdog_backend.domain.repository.AnimalRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.TutorRepository;
import com.senai.nprrs_tagdog_backend.infrastructure.security.JwtService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AnimalControllerIntegrationTest {
    //Esses testes foram feitos por Rafael Borges Gibertoni

    @LocalServerPort
    private int port;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private JwtService jwtService;

    private String tokenAdmin;
    private Tutor tutorSalvo;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        // Limpeza (Animal primeiro por causa da FK para Tutor)
        tutorRepository.deleteAll();
        animalRepository.deleteAll();

        // 1. Criar Tutor preenchendo o campo ROLE (Correção do erro 23502)
        Tutor tutor = new Tutor();
        tutor.setNome("Admin Teste");
        tutor.setEmail("tutor@email.com");
        tutor.setCpf("12345678901");
        tutor.setSenha("senha123");
        tutor.setAtivo(true);
        tutor.setRole(Role.valueOf("ADMIN")); // Certifique-se que o nome do método é esse na sua entidade

        tutorSalvo = tutorRepository.save(tutor);

        // 2. Gerar Token para as requisições
        tokenAdmin = "Bearer " + jwtService.generateToken(tutorSalvo.getEmail(), "ADMIN");
    }

    @Test
    @DisplayName("Deve cadastrar um animal via API com sucesso")
    void deveCadastrarAnimalComSucesso() {
        AnimalDTO.AnimalRegistroDTO payload = new AnimalDTO.AnimalRegistroDTO(
                "", "Bob", "Golden Retriever", SexoAnimal.MACHO,
                PorteAnimal.GRANDE, LocalDate.of(2026, 2, 19), "Alergia a chocolate"
        );

        given()
                .header("Authorization", tokenAdmin)
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/animais/emailOuCpfTutor/{email}", tutorSalvo.getEmail())
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("nome", equalTo("Bob"))
                .body("raca", equalTo("Golden Retriever"));
    }

    @Test
    @DisplayName("Deve buscar animal por matrícula via API")
    void deveBuscarPorMatricula() {
        // Primeiro cadastramos um animal
        AnimalDTO.AnimalResponseDTO animalCriado = cadastrarAnimalMock();

        given()
                .header("Authorization", tokenAdmin)
                .when()
                .get("/api/animais/matricula/{matricula}", animalCriado.matricula())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("nome", equalTo("Bob"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar matrícula inexistente")
    void deveRetornar404ParaMatriculaInexistente() {
        given()
                .header("Authorization", tokenAdmin)
                .when()
                .get("/api/animais/matricula/{matricula}", "TD-99999")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Deve atualizar a tag de um animal")
    void deveAtualizarTag() {
        AnimalDTO.AnimalResponseDTO animalCriado = cadastrarAnimalMock();
        String novaTag = "TAG-2026";

        given()
                .header("Authorization", tokenAdmin)
                .when()
                .put("/api/animais/matricula/{matricula}/tag/{tag}", animalCriado.matricula(), novaTag)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("numeroTag", equalTo(novaTag)); // Ajuste o campo conforme seu DTO
    }

    @Test
    @DisplayName("Deve realizar soft delete do animal")
    void deveDeletarAnimal() {
        AnimalDTO.AnimalResponseDTO animalCriado = cadastrarAnimalMock();

        given()
                .header("Authorization", tokenAdmin)
                .when()
                .delete("/api/animais/deletar/matricula/{matricula}", animalCriado.matricula())
                .then()
                .statusCode(200);
        // Se seu controller for 'void', o Spring retorna 204 ou 200.
    }

    // Método auxiliar para criar um animal e retornar o DTO de resposta
    private AnimalDTO.AnimalResponseDTO cadastrarAnimalMock() {
        AnimalDTO.AnimalRegistroDTO payload = new AnimalDTO.AnimalRegistroDTO(
                "", "Bob", "Golden Retriever", SexoAnimal.MACHO,
                PorteAnimal.GRANDE, LocalDate.now(), "Desc"
        );

        return given()
                .header("Authorization", tokenAdmin)
                .contentType(ContentType.JSON)
                .body(payload)
                .post("/api/animais/emailOuCpfTutor/{email}", tutorSalvo.getEmail())
                .then()
                .extract()
                .as(AnimalDTO.AnimalResponseDTO.class);
    }
}