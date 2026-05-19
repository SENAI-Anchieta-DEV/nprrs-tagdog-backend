package com.senai.nprrs_tagdog_backend.integration.controller;

import com.senai.nprrs_tagdog_backend.application.dto.AdminDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Admin;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import com.senai.nprrs_tagdog_backend.domain.repository.AdminRepository;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdminControllerIntegrationTest {
    //Devido a um erro de merge, o nome do autor foi sobrescrito. Esses testes foram feitos por Sabrina Matos Almeida

    @LocalServerPort
    private int port;

    @Autowired
    private AdminRepository repository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String token;

    @BeforeEach
    void setUp() {

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        repository.deleteAll();

        Admin admin = new Admin();

        admin.setNome("Admin");
        admin.setEmail("admin@email.com");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setRole(Role.ADMIN);

        repository.save(admin);

        token = jwtService.generateToken(
                "admin@email.com",
                "ADMIN"
        );
    }

    @Test
    @DisplayName("Deve cadastrar administrador via API")
    void deveCadastrarAdministradorViaApi() {

        AdminDTO.AdminRegistroDTO payload =
                new AdminDTO.AdminRegistroDTO(
                        "Sabrina",
                        "sabrina@email.com",
                        "123456"
                );

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(payload)
                .when()
                .post("/api/admin")
                .then()
                .statusCode(201)
                .body("nome", equalTo("Sabrina"));
    }

    @Test
    @DisplayName("Deve listar administradores")
    void deveListarAdministradores() {

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/admin")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Deve buscar administrador por email")
    void deveBuscarAdministradorPorEmail() {

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/admin/email/admin@email.com")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Deve atualizar administrador via API")
    void deveAtualizarAdministrador() {

        Admin admin = new Admin();
        admin.setNome("Sabrina");
        admin.setEmail("sabrina@email.com");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setRole(Role.ADMIN);

        repository.save(admin);

        String token = jwtService.generateToken(
                "sabrina@email.com",
                "ADMIN"
        );

        AdminDTO.AdminRegistroDTO payload =
                new AdminDTO.AdminRegistroDTO(
                        "Sabrina Atualizada",
                        "sabrina@email.com",
                        "654321"
                );

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(payload)

                .when()
                .put("/api/admin/email/{email}", "sabrina@email.com")

                .then()
                .statusCode(200)
                .body("nome", equalTo("Sabrina Atualizada"))
                .body("email", equalTo("sabrina@email.com"));
    }

    @Test
    @DisplayName("Deve desativar administrador")
    void deveDesativarAdministrador() {

        Admin admin = new Admin();
        admin.setNome("Sabrina");
        admin.setEmail("sabrina@email.com");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setRole(Role.ADMIN);

        repository.save(admin);

        String token = jwtService.generateToken(
                "sabrina@email.com",
                "ADMIN"
        );

        given()
                .header("Authorization", "Bearer " + token)

                .when()
                .delete("/api/admin/email/{email}", "sabrina@email.com")

                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Não deve acessar sem token")
    void naoDeveAcessarSemToken() {

        given()
                .when()
                .get("/api/admin")
                .then()
                .statusCode(403);
    }
}