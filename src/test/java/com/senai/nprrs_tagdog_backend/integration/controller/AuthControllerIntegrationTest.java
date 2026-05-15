package com.senai.nprrs_tagdog_backend.integration.controller;

import com.senai.nprrs_tagdog_backend.application.dto.AuthDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Admin;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import com.senai.nprrs_tagdog_backend.domain.repository.AdminRepository;

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

import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest {
    //Esses testes foram feitos por Sabrina Matos Almeida

    @LocalServerPort
    private int port;

    @Autowired
    private AdminRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        repository.deleteAll();
    }

    @Test
    @DisplayName("Deve realizar login via API")
    void deveRealizarLoginViaApi() {

        Admin admin = new Admin();

        admin.setNome("Admin");
        admin.setEmail("admin@email.com");
        admin.setSenha(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        admin.setAtivo(true);

        repository.save(admin);

        AuthDTO.LoginRequest payload =
                new AuthDTO.LoginRequest(
                        "admin@email.com",
                        "admin123"
                );

        given()
                .contentType(ContentType.JSON)
                .body(payload)

                .when()
                .post("/auth/login")

                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }
    @Test
    @DisplayName("Não deve logar com senha inválida")
    void naoDeveLogarSenhaInvalida() {

        Admin admin = new Admin();
        admin.setNome("Admin");
        admin.setEmail("admin@email.com");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setRole(Role.ADMIN);

        repository.save(admin);

        AuthDTO.LoginRequest payload =
                new AuthDTO.LoginRequest(
                        "admin@email.com",
                        "senhaErrada"
                );

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(404);
    }
    @Test
    @DisplayName("Não deve realizar login com usuário inexistente")
    void naoDeveLogarUsuarioInexistente() {

        AuthDTO.LoginRequest payload =
                new AuthDTO.LoginRequest(
                        "naoexiste@email.com",
                        "123456"
                );

        given()
                .contentType(ContentType.JSON)
                .body(payload)

                .when()
                .post("/auth/login")

                .then()
                .statusCode(404);
    }
}