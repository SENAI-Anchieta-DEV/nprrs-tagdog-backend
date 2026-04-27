package com.senai.nprrs_tagdog_backend.integration.controller;

import com.senai.nprrs_tagdog_backend.application.dto.AdminDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Admin;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import com.senai.nprrs_tagdog_backend.domain.repository.AdminRepository;
import com.senai.nprrs_tagdog_backend.infrastructure.security.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
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

        @LocalServerPort
        private int port;

        @Autowired
        private AdminRepository repository;

        @Autowired
        private JwtService jwtService;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @BeforeEach
        void setUp() {
            RestAssured.baseURI = "http://localhost";
            RestAssured.port = port;
            repository.deleteAll();
        }


    @Test
    @DisplayName("Deve cadastrar administrador via API")
    void deveCadastrarAdministradorViaApi() {
        AdminDTO.AdminRegistroDTO payload = new AdminDTO.AdminRegistroDTO("Fabiano Peixoto", "fabianopeixoto@email.com", "123456");

        Admin admin = new Admin();
        admin.setNome("Admin");
        admin.setEmail("admin@email.com");
        admin.setSenha(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);

        repository.save(admin);

        String token = jwtService.generateToken("admin@email.com", "ADMIN");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(payload)
                .when()
                .post("/api/admin")
                .then()
                .statusCode(201)
                .body("nome", equalTo("Fabiano Peixoto"))
                .body("email", equalTo("fabianopeixoto@email.com"));
    }


    }
