package com.senai.nprrs_tagdog_backend.integration.controller;

import com.senai.nprrs_tagdog_backend.application.dto.FuncionarioDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.*;
import com.senai.nprrs_tagdog_backend.domain.repository.AdminRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.AnimalRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.FuncionarioRepository;
import com.senai.nprrs_tagdog_backend.infrastructure.security.JwtService;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FuncionarioControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RequestSpecification requestSpecification;

    private static final String ADMIN_EMAIL = "admin@email.com";
    private static final String FUNCIONARIO_EMAIL = "funcionario@email.com";
    private static final String MATRICULA_ANIMAL = "TD-12345";

    @BeforeEach
    void setUp() {

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();

        limparBanco();
        criarAdmin();
    }

    private RequestSpecification requestComAuth(String token) {
        return given()
                .spec(requestSpecification)
                .header("Authorization", "Bearer " + token);
    }

    private String gerarTokenAdmin() {
        return jwtService.generateToken(ADMIN_EMAIL, "ADMIN");
    }

    private void limparBanco() {
        adminRepository.deleteAll();
        funcionarioRepository.deleteAll();
        animalRepository.deleteAll();
    }

    private void criarAdmin() {

        Admin admin = new Admin();

        admin.setNome("Admin");
        admin.setEmail(ADMIN_EMAIL);
        admin.setSenha(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);

        adminRepository.save(admin);
    }

    private Funcionario criarFuncionario() {

        Funcionario funcionario = new Funcionario();

        funcionario.setNome("F");
        funcionario.setEmail(FUNCIONARIO_EMAIL);
        funcionario.setSenha(passwordEncoder.encode("123"));
        funcionario.setRole(Role.FUNCIONARIO);
        funcionario.setAtivo(true);

        return funcionarioRepository.save(funcionario);
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

        return animalRepository.save(animal);
    }

    @Test
    @DisplayName("Deve cadastrar funcionário")
    void deveCadastrarFuncionario() {
        String token = gerarTokenAdmin();

        FuncionarioDTO.FuncionarioRegistroDTO payload =
                new FuncionarioDTO.FuncionarioRegistroDTO(
                        "Nome Funcionario",
                        FUNCIONARIO_EMAIL,
                        "123456"
                );

        requestComAuth(token)
                .body(payload)
                .when()
                .post("/api/funcionarios")
                .then()
                .statusCode(201)
                .body("nome", equalTo("Nome Funcionario"))
                .body("email", equalTo(FUNCIONARIO_EMAIL));
    }

    @Test
    @DisplayName("Deve retornar 409 ao cadastrar funcionário duplicado")
    void deveRetornar409QuandoFuncionarioDuplicadoNoCadastro() {
        String token = gerarTokenAdmin();

        criarFuncionario();

        FuncionarioDTO.FuncionarioRegistroDTO payload =
                new FuncionarioDTO.FuncionarioRegistroDTO(
                        "Nome Funcionario",
                        FUNCIONARIO_EMAIL,
                        "123456"
                );

        requestComAuth(token)
                .body(payload)
                .when()
                .post("/api/funcionarios")
                .then()
                .statusCode(409);
    }

    @Test
    @DisplayName("Deve cadastrar animal sob cuidado do funcionário")
    void deveCadastrarAnimalNoFuncionario() {
        String token = gerarTokenAdmin();

        Funcionario funcionario = criarFuncionario();
        Animal animal = criarAnimal();

        requestComAuth(token)
                .when()
                .post(
                        "/api/funcionarios/email/{email}/animalMatricula/{matricula}",
                        funcionario.getEmail(),
                        animal.getMatricula()
                )
                .then()
                .statusCode(201)

                .body("nome", equalTo("F"))
                .body("email", equalTo(FUNCIONARIO_EMAIL))
                .body("ativo", equalTo(true))
                .body("role", equalTo("FUNCIONARIO"))

                .body("animais.size()", equalTo(1))

                .body("animais[0].imagem", nullValue())
                .body("animais[0].matricula", equalTo(MATRICULA_ANIMAL))
                .body("animais[0].nome", equalTo("Nome Animal"))
                .body("animais[0].raca", equalTo("Nome Raca"))
                .body("animais[0].sexo", equalTo("FEMEA"))
                .body("animais[0].porte", equalTo("MEDIO"))
                .body("animais[0].dataNascimento", equalTo("2026-05-08"))
                .body("animais[0].descricao", equalTo("Descricao"))
                .body("animais[0].numeroTag", nullValue())
                .body("animais[0].checkInCheckOut", empty())
                .body("animais[0].ativo", equalTo(true));
    }

    @Test
    @DisplayName("Deve retornar 404 ao cadastrar animal em funcionário inexistente")
    void deveRetornar404QuandoFuncionarioNaoEncontradoNoCadastroAnimal() {
        String token = gerarTokenAdmin();

        criarAnimal();

        requestComAuth(token)
                .when()
                .post(
                        "/api/funcionarios/email/{email}/animalMatricula/{matricula}",
                        FUNCIONARIO_EMAIL,
                        MATRICULA_ANIMAL
                )
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Deve retornar  404 ao cadastrar animal com animal inexistente")
    void deveRetornar404QuandoAnimalNaoEncontradoNoCadastroAnimal() {
        String token = gerarTokenAdmin();

        Funcionario funcionario = criarFuncionario();

        requestComAuth(token)
                .when()
                .post(
                        "/api/funcionarios/email/{email}/animalMatricula/{matricula}",
                        funcionario.getEmail(),
                        MATRICULA_ANIMAL
                )
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Deve listar todos os funcionários ativos")
    void deveListarFuncionariosAtivos() {
        String token = gerarTokenAdmin();

        criarFuncionario();

        requestComAuth(token)
                .when()
                .get("/api/funcionarios")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].nome", equalTo("F"))
                .body("[0].email", equalTo(FUNCIONARIO_EMAIL))
                .body("[0].ativo", equalTo(true))
                .body("[0].role", equalTo("FUNCIONARIO"));
    }

    @Test
    @DisplayName("Deve buscar funcionário ativo por email")
    void deveBuscarFuncionarioPorEmail() {
        String token = gerarTokenAdmin();

        criarFuncionario();

        requestComAuth(token)
                .when()
                .get("/api/funcionarios/email/{email}", FUNCIONARIO_EMAIL)
                .then()
                .statusCode(200)
                .body("nome", equalTo("F"))
                .body("email", equalTo(FUNCIONARIO_EMAIL))
                .body("ativo", equalTo(true))
                .body("role", equalTo("FUNCIONARIO"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar funcionário inexistente")
    void deveRetornar404QuandoFuncionarioNaoEncontradoNaBuscaPorEmail() {
        String token = gerarTokenAdmin();

        requestComAuth(token)
                .when()
                .get("/api/funcionarios/email/{email}", FUNCIONARIO_EMAIL)
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Deve atualizar funcionário")
    void deveAtualizarFuncionario() {
        String token = gerarTokenAdmin();

        criarFuncionario();

        FuncionarioDTO.FuncionarioAtualizarDTO payload =
                new FuncionarioDTO.FuncionarioAtualizarDTO(
                        "Funcionario Atualizado",
                        "novoemail@email.com",
                        "novasenha"
                );

        requestComAuth(token)
                .body(payload)
                .when()
                .put("/api/funcionarios/email/{email}", FUNCIONARIO_EMAIL)
                .then()
                .statusCode(200)
                .body("nome", equalTo("Funcionario Atualizado"))
                .body("email", equalTo("novoemail@email.com"))
                .body("ativo", equalTo(true))
                .body("role", equalTo("FUNCIONARIO"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao atualizar funcionário inexistente")
    void deveRetornar404QuandoFuncionarioNaoEncontradoAtualizar() {
        String token = gerarTokenAdmin();

        FuncionarioDTO.FuncionarioAtualizarDTO payload =
                new FuncionarioDTO.FuncionarioAtualizarDTO(
                        "Funcionario Atualizado",
                        "novoemail@email.com",
                        "novasenha"
                );

        requestComAuth(token)
                .body(payload)
                .when()
                .put("/api/funcionarios/email/{email}", FUNCIONARIO_EMAIL)
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Deve desativar funcionário")
    void deveDesativarFuncionario() {
        String token = gerarTokenAdmin();

        criarFuncionario();

        requestComAuth(token)
                .when()
                .delete("/api/funcionarios/email/{email}", FUNCIONARIO_EMAIL)
                .then()
                .statusCode(204);

        Funcionario funcionario =
                funcionarioRepository.findByEmail(FUNCIONARIO_EMAIL).orElseThrow();

        assertFalse(funcionario.isAtivo());
    }

    @Test
    @DisplayName("Deve retornar 404 ao desativar funcionário inexistente")
    void deveRetornar404QuandoFuncionarioNaoEncontradoDeletar() {
        String token = gerarTokenAdmin();

        requestComAuth(token)
                .when()
                .delete("/api/funcionarios/email/{email}", FUNCIONARIO_EMAIL)
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Deve retirar animal do funcionário")
    void deveRetirarAnimalDoFuncionario() {
        String token = gerarTokenAdmin();

        Funcionario funcionario = criarFuncionario();

        if (funcionario.getAnimais() == null) {
            funcionario.setAnimais(new ArrayList<>());
        }

        Animal animal = criarAnimal();

        funcionario.getAnimais().add(animal);
        funcionarioRepository.save(funcionario);

        requestComAuth(token)
                .when()
                .put(
                        "/api/funcionarios/email/{email}/matriculaAnimal/{matricula}",
                        funcionario.getEmail(),
                        animal.getMatricula()
                )
                .then()
                .statusCode(204);

        Funcionario funcionarioAtualizado =
                funcionarioRepository.findByEmail(FUNCIONARIO_EMAIL).orElseThrow();

        assertTrue(funcionarioAtualizado.getAnimais().isEmpty());
    }

    @Test
    @DisplayName("Deve retornar 404 ao retirar animal de funcionário inexistente")
    void deveRetornar404QuandoFuncionarioNaoEncontradoAtualizarRetirarAnimal() {
        String token = gerarTokenAdmin();

        criarAnimal();

        requestComAuth(token)
                .when()
                .put(
                        "/api/funcionarios/email/{email}/matriculaAnimal/{matricula}",
                        FUNCIONARIO_EMAIL,
                        MATRICULA_ANIMAL
                )
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Deve retornar 404 ao retirar animal inexistente")
    void deveRetornar404QuandoAnimalNaoEncontradoAtualizarRetirarAnimal() {
        String token = gerarTokenAdmin();

        criarFuncionario();

        requestComAuth(token)
                .when()
                .put(
                        "/api/funcionarios/email/{email}/matriculaAnimal/{matricula}",
                        FUNCIONARIO_EMAIL,
                        MATRICULA_ANIMAL
                )
                .then()
                .statusCode(404);
    }

}

//Feito por Raquel Yukie Tsuji
