package com.senai.nprrs_tagdog_backend.integration.controller;

import com.senai.nprrs_tagdog_backend.application.dto.TutorDTO;
import com.senai.nprrs_tagdog_backend.application.dto.AnimalDTO;
import com.senai.nprrs_tagdog_backend.application.dto.EnderecoDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Admin;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import com.senai.nprrs_tagdog_backend.domain.entity.SexoAnimal;
import com.senai.nprrs_tagdog_backend.domain.entity.PorteAnimal;
import com.senai.nprrs_tagdog_backend.domain.repository.AdminRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.TutorRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.AnimalRepository;
import com.senai.nprrs_tagdog_backend.infrastructure.security.JwtService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TutorControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        animalRepository.deleteAll();
        tutorRepository.deleteAll();
        adminRepository.deleteAll();

        Admin admin = new Admin();
        admin.setNome("Admin Teste");
        admin.setEmail("admin@teste.com");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setRole(Role.ADMIN);
        adminRepository.save(admin);

        adminToken = jwtService.generateToken("admin@teste.com", "ADMIN");
    }

    @Test
    @DisplayName("Deve registrar um novo tutor com animal e endereço com sucesso")
    void deveRegistrarTutorViaApi() {

        AnimalDTO.AnimalRegistroDTO animalDTO = new AnimalDTO.AnimalRegistroDTO(
                "http://link-imagem.com/foto.jpg",
                "Bob",
                "Golden Retriever",
                SexoAnimal.MACHO,
                PorteAnimal.GRANDE,
                LocalDate.of(2022, 5, 20),
                "Cão muito dócil e brincalhão"
        );

        EnderecoDTO.EnderecoRegistroDTO enderecoDTO = new EnderecoDTO.EnderecoRegistroDTO(
                "88000-000", "Rua das Flores", "Bairro Norte", "Florianópolis", "SC", "100", "Casa"
        );

        // ORDEM CORRIGIDA CONFORME SEU RECORD TutorRegistroDTO:
        // 1. nome, 2. email, 3. senha, 4. cpf, 5. telefone, 6. endereco, 7. animal
        TutorDTO.TutorRegistroDTO payload = new TutorDTO.TutorRegistroDTO(
                "João Silva",           // nome
                "joao.silva@email.com", // email
                "senha123",             // senha (TERCEIRO PARÂMETRO)
                "123.456.789-00",       // cpf
                "(48) 99999-9999",      // telefone
                enderecoDTO,            // endereco
                animalDTO               // animal
        );

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(payload)
                .when()
                .post("/api/tutor")
                .then()
                .log().all() // Mantido para você ver o sucesso ou detalhe de erro
                .statusCode(201)
                .body("nome", is("João Silva"))
                .body("email", is("joao.silva@email.com"))
                .body("cpf", is("123.456.789-00"))
                .body("animais", hasSize(greaterThanOrEqualTo(1)))
                .body("animais[0].nome", is("Bob"));
    }}