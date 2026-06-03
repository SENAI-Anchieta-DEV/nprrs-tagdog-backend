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
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TutorControllerIntegrationTest {
    //Devido a um erro de merge, o nome do autor foi sobrescrito. Esses testes foram feitos por Pietra Rainone Rocha

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
                "Cão muito dócil e brincalhão",
                ""
        );

        EnderecoDTO.EnderecoRegistroDTO enderecoDTO = new EnderecoDTO.EnderecoRegistroDTO(
                "88000-000", "Rua das Flores", "Bairro Norte", "Florianópolis", "SC", "100", "Casa"
        );

        List<AnimalDTO.AnimalRegistroDTO> animais = new ArrayList<>();
        animais.add(animalDTO);
        TutorDTO.TutorRegistroDTO payload = new TutorDTO.TutorRegistroDTO(
                "João Silva",
                "joao.silva@email.com",
                "senha123",
                "123.456.789-00",
                "(48) 99999-9999",
                enderecoDTO,
                animais
        );

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(payload)
                .when()
                .post("/api/tutores")
                .then()
                .log().all()
                .statusCode(201)
                .body("nome", is("João Silva"))
                .body("email", is("joao.silva@email.com"))
                .body("cpf", is("123.456.789-00"))

                .body("animais[0].nome", is("Bob"));
    }}