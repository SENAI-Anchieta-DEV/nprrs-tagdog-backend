package com.senai.nprrs_tagdog_backend.unit.service;

import com.senai.nprrs_tagdog_backend.application.dto.TagDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.*;
import com.senai.nprrs_tagdog_backend.domain.repository.*;
import com.senai.nprrs_tagdog_backend.application.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TagServiceTest {
    //Esses testes foram feitos por Raquel Yukie Tsuji

    @Mock private AnimalRepository animalRepository;
    @Mock private LocalCoordenadasRepository localCoordenadasRepository;
    @Mock private TutorRepository tutorRepository;
    @Mock private FuncionarioRepository funcionarioRepository;
    @Mock private AdminRepository adminRepository;
    @Mock private TagRepository tagRepository;
    @Mock private JavaMailSender mailSender;

    @InjectMocks private TagService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private TagDTO.TagRegistroDTO criarTagDTO(String numero, String latitude, String longitude) {
        return new TagDTO.TagRegistroDTO(numero, 0, 0, " ", " ", latitude, longitude, 0.0, false, " ", " ", 0, "25/03/2026 11:00:00");
    }

    private Animal criarAnimal(String numeroTag) {
        return Animal.builder()
                .matricula("A-123")
                .nome("Rex")
                .numeroTag(numeroTag)
                .build();
    }

    private LocalCoordenadas criarLocal() {
        return LocalCoordenadas.builder()
                .latitude("0")
                .longitude("0")
                .raio(50)
                .build();
    }

    @Test
    @DisplayName("Deve salvar tag pela primeira vez")
    void deveSalvarTagPrimeiraVez() {
        TagDTO.TagRegistroDTO dto = criarTagDTO("TAG-001", "0", "0");
        Animal animal = criarAnimal("TAG-001");
        LocalCoordenadas local = criarLocal();

        when(animalRepository.findByNumeroTag("TAG-001")).thenReturn(animal);
        when(localCoordenadasRepository.findAll()).thenReturn(List.of(local));
        when(tagRepository.findFirstByNumeroOrderByDataCriadoDesc("TAG-001")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.salvar(dto);

        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    @DisplayName("Deve salvar tag se animal se moveu mais que distância mínima")
    void deveSalvarTagSeDistanciaMaiorQueMinima() {
        TagDTO.TagRegistroDTO dto = criarTagDTO("TAG-002", "0.0002", "0.0002");
        Animal animal = criarAnimal("TAG-002");
        LocalCoordenadas local = criarLocal();

        Tag ultimaTag = Tag.builder().numero("TAG-002").latitude("0").longitude("0")
                .dataCriado(LocalDateTime.now().minusMinutes(10))
                .ativo(true)
                .saidaNaoAutorizada(false)
                .build();

        when(animalRepository.findByNumeroTag("TAG-002")).thenReturn(animal);
        when(localCoordenadasRepository.findAll()).thenReturn(List.of(local));
        when(tagRepository.findFirstByNumeroOrderByDataCriadoDesc("TAG-002")).thenReturn(Optional.of(ultimaTag));
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.salvar(dto);

        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    @DisplayName("Deve marcar saída não autorizada e enviar email")
    void deveMarcarSaidaNaoAutorizadaEEnviarEmail() {
        TagDTO.TagRegistroDTO dto = criarTagDTO("TAG-003", "100", "100");
        Animal animal = criarAnimal("TAG-003");
        LocalCoordenadas local = criarLocal();
        Tutor tutor = Tutor.builder().nome("João").email("joao@email.com").animais(List.of(animal)).build();
        Admin admin = Admin.builder().nome("Admin").email("admin@email.com").build();

        when(animalRepository.findByNumeroTag("TAG-003")).thenReturn(animal);
        when(localCoordenadasRepository.findAll()).thenReturn(List.of(local));
        when(tagRepository.findFirstByNumeroOrderByDataCriadoDesc("TAG-003")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tutorRepository.findByAnimais(animal)).thenReturn(tutor);
        when(adminRepository.findAll()).thenReturn(List.of(admin));

        service.salvar(dto);

        ArgumentCaptor<Tag> tagCaptor = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepository).save(tagCaptor.capture());
        assertTrue(tagCaptor.getValue().isSaidaNaoAutorizada());
    }

    @Test
    @DisplayName("Calcular distância corretamente")
    void deveCalcularDistancia() {
        double distancia = TagService.calcularDistanciaEmMetros(0,0,0,0.001);
        assertTrue(distancia > 0);
    }

    @Test
    @DisplayName("Verificar isForaDoLocalAutorizado")
    void deveDetectarForaDoLocalAutorizado() {
        Tag tag = Tag.builder().latitude("100").longitude("100").build();
        LocalCoordenadas local = LocalCoordenadas.builder().latitude("0").longitude("0").raio(50).build();
        assertTrue(service.isForaDoLocalAutorizado(tag, local));
    }
}