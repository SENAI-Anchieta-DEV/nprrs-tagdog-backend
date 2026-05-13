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

        // Limpeza na ordem correta (filhos primeiro se não houver Cascade)
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

        // 1. Verifique se o formato da data (2022-05-20) é aceito pelo seu banco/DTO.
        // Se der erro de data, tente usar LocalDate.now() apenas para testar.
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

        TutorDTO.TutorRegistroDTO payload = new TutorDTO.TutorRegistroDTO(
                "João Silva",
                "joao.silva@email.com",
                "senha123",
                "123.456.789-00",
                "(48) 99999-9999",
                enderecoDTO,
                animalDTO
        );

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(payload)
                .when()
                .post("/api/tutores") // CORREÇÃO 1: Adicionado o "es" no final da rota
                .then()
                .log().all()
                .statusCode(201)
                .body("nome", is("João Silva"))
                .body("email", is("joao.silva@email.com"))
                .body("cpf", is("123.456.789-00"))
                // CORREÇÃO 2: Verifique se o retorno vem como 'animais' (lista) ou 'animal' (objeto).
                // Com base no seu DTO de registro, geralmente o retorno segue o nome da lista na Entity.
                .body("animais[0].nome", is("Bob"));
    }}