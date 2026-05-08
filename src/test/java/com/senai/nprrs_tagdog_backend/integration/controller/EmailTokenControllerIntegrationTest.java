package com.senai.nprrs_tagdog_backend.integration.controller;

import com.senai.nprrs_tagdog_backend.application.dto.SenhaDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Admin;
import com.senai.nprrs_tagdog_backend.domain.entity.EmailToken;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import com.senai.nprrs_tagdog_backend.domain.entity.Usuario;
import com.senai.nprrs_tagdog_backend.domain.repository.EmailTokenRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.UsuarioRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "management.health.mail.enabled=false"
)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false) // 🔥 DESABILITA SECURITY NO TEST
class EmailTokenControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private EmailTokenRepository emailTokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        emailTokenRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar um token e retornar 201 ao solicitar via email")
    void deveCriarTokenComSucesso() {
        String emailExemplo = "tutor@email.com";
        criarUsuarioConcretoNoBanco(emailExemplo);

        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/api/emailtoken/email/{email}", emailExemplo)
                .then()
                .statusCode(201)
                .body("token", notNullValue());

        Assertions.assertEquals(1, emailTokenRepository.count());
    }

    @Test
    @DisplayName("Deve validar o token e retornar 204 ao alterar senha")
    void deveValidarTokenComSucesso() {
        String email = "tutor@email.com";
        String tokenValidoStr = "123456";
        Integer tokenValidoInt = Integer.parseInt(tokenValidoStr);

        Usuario usuario = criarUsuarioConcretoNoBanco(email);

        EmailToken emailToken = new EmailToken();
        emailToken.setToken(tokenValidoInt);
        emailToken.setDataCriado(LocalDateTime.now());
        emailToken.setDataExpirado(LocalDateTime.now().plusHours(1));
        emailToken.setUsuario(usuario);

        emailTokenRepository.save(emailToken);

        SenhaDTO senhaDTO = new SenhaDTO(email, tokenValidoStr, "novaSenha123");

        given()
                .contentType(ContentType.JSON)
                .body(senhaDTO)
                .when()
                .put("/api/emailtoken/token")
                .then()
                .statusCode(204); // ✅ CORRETO: esperado no fluxo de sucesso

        Usuario usuarioNoBanco = usuarioRepository.findByEmail(email).orElseThrow();

        Assertions.assertTrue(
                passwordEncoder.matches("novaSenha123", usuarioNoBanco.getSenha())
        );

        Assertions.assertEquals(0, emailTokenRepository.count());
    }

    private Usuario criarUsuarioConcretoNoBanco(String email) {
        Admin admin = new Admin();
        admin.setEmail(email);
        admin.setNome("Usuário Teste");
        admin.setSenha(passwordEncoder.encode("senhaAntiga123"));
        admin.setRole(Role.ADMIN);

        return usuarioRepository.save(admin);
    }
}
