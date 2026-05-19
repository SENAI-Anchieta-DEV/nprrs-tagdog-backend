package com.senai.nprrs_tagdog_backend.unit.service;

import com.senai.nprrs_tagdog_backend.application.dto.FuncionarioDTO;
import com.senai.nprrs_tagdog_backend.application.service.FuncionarioService;
import com.senai.nprrs_tagdog_backend.domain.entity.Animal;
import com.senai.nprrs_tagdog_backend.domain.entity.Funcionario;
import com.senai.nprrs_tagdog_backend.domain.entity.Role;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeDuplicadaException;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeNaoEncontradaException;
import com.senai.nprrs_tagdog_backend.domain.exceptions.RegraNegocioException;
import com.senai.nprrs_tagdog_backend.domain.repository.AnimalRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private FuncionarioService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new FuncionarioService(
                funcionarioRepository,
                animalRepository,
                passwordEncoder
        );
    }

    private FuncionarioDTO.FuncionarioRegistroDTO criarFuncionarioValido() {
        return new FuncionarioDTO.FuncionarioRegistroDTO(
                "João",
                "joao@email.com",
                "123456"
        );
    }

    private Funcionario criarFuncionarioEntity() {
        return Funcionario.builder()
                .id("1")
                .nome("João")
                .email("joao@email.com")
                .senha("senha-criptografada")
                .ativo(true)
                .role(Role.FUNCIONARIO)
                .animais(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Deve cadastrar funcionário quando dados forem válidos")
    void deveCadastrarFuncionarioQuandoDadosForemValidos() {

        FuncionarioDTO.FuncionarioRegistroDTO dto =
                criarFuncionarioValido();

        Funcionario funcionario = criarFuncionarioEntity();

        when(funcionarioRepository.findByEmail(dto.email()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(dto.senha()))
                .thenReturn("senha-criptografada");

        when(funcionarioRepository.save(any(Funcionario.class)))
                .thenReturn(funcionario);

        FuncionarioDTO.FuncionarioResponseDTO response =
                service.registrarFuncionario(dto);

        assertNotNull(response);
        assertEquals("João", response.nome());
        assertEquals("joao@email.com", response.email());

        verify(funcionarioRepository, times(1))
                .save(any(Funcionario.class));

        verify(passwordEncoder, times(1))
                .encode(dto.senha());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existir")
    void deveLancarExcecaoQuandoEmailJaExistir() {

        FuncionarioDTO.FuncionarioRegistroDTO dto =
                criarFuncionarioValido();

        when(funcionarioRepository.findByEmail(dto.email()))
                .thenReturn(Optional.of(criarFuncionarioEntity()));

        EntidadeDuplicadaException exception = assertThrows(
                EntidadeDuplicadaException.class,
                () -> service.registrarFuncionario(dto)
        );

        assertEquals(
                "Funcionário com este email já cadastrada. ",
                exception.getMessage()
        );

        verify(funcionarioRepository, never())
                .save(any(Funcionario.class));
    }

    @Test
    @DisplayName("Deve listar funcionários")
    void deveListarFuncionarios() {

        List<Funcionario> funcionarios =
                List.of(criarFuncionarioEntity());

        when(funcionarioRepository.findAll())
                .thenReturn(funcionarios);

        List<FuncionarioDTO.FuncionarioResponseDTO> response =
                service.listarFuncionarios();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("João", response.get(0).nome());

        verify(funcionarioRepository, times(1))
                .findAll();
    }

    @Test
    @DisplayName("Deve buscar funcionário por email")
    void deveBuscarFuncionarioPorEmail() {

        Funcionario funcionario = criarFuncionarioEntity();

        when(funcionarioRepository.findByEmail("joao@email.com"))
                .thenReturn(Optional.of(funcionario));

        FuncionarioDTO.FuncionarioResponseDTO response =
                service.buscarFuncionarioEmail("joao@email.com");

        assertNotNull(response);
        assertEquals("João", response.nome());
        assertEquals("joao@email.com", response.email());

        verify(funcionarioRepository, times(1))
                .findByEmail("joao@email.com");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar funcionário inexistente")
    void deveLancarExcecaoAoBuscarFuncionarioInexistente() {

        when(funcionarioRepository.findByEmail("inexistente@email.com"))
                .thenReturn(Optional.empty());

        EntidadeNaoEncontradaException exception = assertThrows(
                EntidadeNaoEncontradaException.class,
                () -> service.buscarFuncionarioEmail(
                        "inexistente@email.com"
                )
        );

        assertEquals("Funcionárionão encontrada.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar funcionário")
    void deveAtualizarFuncionario() {

        Funcionario funcionario = criarFuncionarioEntity();

        FuncionarioDTO.FuncionarioAtualizarDTO dto =
                new FuncionarioDTO.FuncionarioAtualizarDTO(
                        "Novo Nome",
                        "novo@email.com",
                        "novaSenha"
                );

        when(funcionarioRepository.findByEmail("joao@email.com"))
                .thenReturn(Optional.of(funcionario));

        when(passwordEncoder.encode("novaSenha"))
                .thenReturn("senha-nova");

        when(funcionarioRepository.save(any(Funcionario.class)))
                .thenReturn(funcionario);

        FuncionarioDTO.FuncionarioResponseDTO response =
                service.atualizarFuncionario(
                        "joao@email.com",
                        dto
                );

        assertNotNull(response);

        verify(passwordEncoder, times(1))
                .encode("novaSenha");

        verify(funcionarioRepository, times(1))
                .save(any(Funcionario.class));
    }

    @Test
    @DisplayName("Deve desativar funcionário ativo")
    void deveDesativarFuncionarioAtivo() {

        Funcionario funcionario = criarFuncionarioEntity();
        funcionario.setAtivo(true);

        when(funcionarioRepository.findByEmail("joao@email.com"))
                .thenReturn(Optional.of(funcionario));

        service.desativarFuncionario("joao@email.com");

        assertFalse(funcionario.isAtivo());

        verify(funcionarioRepository, atLeastOnce())
                .save(funcionario);
    }

    @Test
    @DisplayName("Deve reativar funcionário inativo")
    void deveReativarFuncionarioInativo() {

        Funcionario funcionario = criarFuncionarioEntity();
        funcionario.setAtivo(false);

        when(funcionarioRepository.findByEmail("joao@email.com"))
                .thenReturn(Optional.of(funcionario));

        service.desativarFuncionario("joao@email.com");

        assertTrue(funcionario.isAtivo());

        verify(funcionarioRepository, atLeastOnce())
                .save(funcionario);
    }

    @Test
    @DisplayName("Deve adicionar animal ao funcionário")
    void deveAdicionarAnimalAoFuncionario() {

        Funcionario funcionario = criarFuncionarioEntity();

        Animal animal = Animal.builder()
                .id("10")
                .matricula("TD-12345")
                .build();

        when(funcionarioRepository.findByEmail("joao@email.com"))
                .thenReturn(Optional.of(funcionario));

        when(animalRepository.findByMatricula("TD-12345"))
                .thenReturn(Optional.of(animal));

        when(funcionarioRepository.save(any(Funcionario.class)))
                .thenReturn(funcionario);

        FuncionarioDTO.FuncionarioResponseDTO response =
                service.adicionarAnimalNoFuncionario(
                        "joao@email.com",
                        "TD-12345"
                );

        assertNotNull(response);

        verify(funcionarioRepository, times(1))
                .save(any(Funcionario.class));

        assertTrue(funcionario.getAnimais().contains(animal));
    }

    @Test
    @DisplayName("Deve remover animal caso funcionário já cuide dele")
    void deveRemoverAnimalCasoFuncionarioJaCuideDele() {

        Animal animal = Animal.builder()
                .id("10")
                .matricula("TD-12345")
                .build();

        Funcionario funcionario = criarFuncionarioEntity();
        funcionario.getAnimais().add(animal);

        when(funcionarioRepository.findByEmail("joao@email.com"))
                .thenReturn(Optional.of(funcionario));

        when(animalRepository.findByMatricula("TD-12345"))
                .thenReturn(Optional.of(animal));

        when(funcionarioRepository.save(any(Funcionario.class)))
                .thenReturn(funcionario);

        service.adicionarAnimalNoFuncionario(
                "joao@email.com",
                "TD-12345"
        );

        assertFalse(funcionario.getAnimais().contains(animal));

        verify(funcionarioRepository, times(1))
                .save(any(Funcionario.class));
    }

    @Test
    @DisplayName("Deve retirar animal do funcionário")
    void deveRetirarAnimalDoFuncionario() {

        Animal animal = Animal.builder()
                .id("10")
                .matricula("TD-12345")
                .build();

        Funcionario funcionario = criarFuncionarioEntity();
        funcionario.getAnimais().add(animal);

        when(funcionarioRepository.findByEmail("joao@email.com"))
                .thenReturn(Optional.of(funcionario));

        when(animalRepository.findByMatricula("TD-12345"))
                .thenReturn(Optional.of(animal));

        service.retirarAnimalDeFuncionario(
                "joao@email.com",
                "TD-12345"
        );

        assertFalse(funcionario.getAnimais().contains(animal));

        verify(funcionarioRepository, times(1))
                .save(funcionario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao retirar animal não associado")
    void deveLancarExcecaoAoRetirarAnimalNaoAssociado() {

        Funcionario funcionario = criarFuncionarioEntity();

        Animal animal = Animal.builder()
                .id("10")
                .matricula("TD-12345")
                .build();

        when(funcionarioRepository.findByEmail("joao@email.com"))
                .thenReturn(Optional.of(funcionario));

        when(animalRepository.findByMatricula("TD-12345"))
                .thenReturn(Optional.of(animal));

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> service.retirarAnimalDeFuncionario(
                        "joao@email.com",
                        "TD-12345"
                )
        );

        assertEquals(
                "Funcionario nao cuida desse animal",
                exception.getMessage()
        );

        verify(funcionarioRepository, never())
                .save(any(Funcionario.class));
    }
}