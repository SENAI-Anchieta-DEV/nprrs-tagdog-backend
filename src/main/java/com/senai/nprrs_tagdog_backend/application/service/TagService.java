package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.TagDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.*;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeNaoEncontradaException;
import com.senai.nprrs_tagdog_backend.domain.exceptions.RegraNegocioException;
import com.senai.nprrs_tagdog_backend.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class TagService {
    private final AnimalRepository animalRepository;
    private final LocalCoordenadasRepository localCoordenadasRepository;
    private final TutorRepository tutorRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final AdminRepository adminRepository;
    private final TagRepository tagRepository;
    private final JavaMailSender mailSender;
    private static final int RAIO_TERRA_METROS = 6371000;
    private static final double DISTANCIA_MINIMA_METROS = 15.0;

    public void salvar(TagDTO.TagRegistroDTO dto) {
        Tag tag = dto.toEntity();

        if (tag.getLatitude() == null || tag.getLongitude() == null) {
            log.info("Tag {} recebida sem coordenadas (Status: {}). Ignorando cálculos.",
                    dto.numero(), dto.statusGps());
            return;
        }

        Animal animal = animalRepository.findByNumeroTag(dto.numero());
        if(animal != null){
            tag.setAnimal(animal);
        }

        if(!tag.isAtivo()){
            tag.setAtivo(true);
        }

        LocalCoordenadas localCoordenadas = localCoordenadasRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Local Coordenadas"));

        if(isForaDoLocalAutorizado(tag, localCoordenadas)){
            tag.setSaidaNaoAutorizada(true);
        }

        Optional<Tag> ultimaTagOpt = tagRepository.findFirstByNumeroOrderByDataCriadoDesc(dto.numero());

        boolean isNovaFuga = false;
        if (tag.isSaidaNaoAutorizada()) {
            if (ultimaTagOpt.isPresent()) {
                isNovaFuga = !ultimaTagOpt.get().isSaidaNaoAutorizada();
            } else {
                isNovaFuga = true;
            }
        }

        if (ultimaTagOpt.isPresent()) {
            Tag ultimaTag = ultimaTagOpt.get();

            try {
                if (ultimaTag.getLatitude() != null && ultimaTag.getLongitude() != null) {
                    double latAtual = Double.parseDouble(tag.getLatitude());
                    double lonAtual = Double.parseDouble(tag.getLongitude());
                    double latAnterior = Double.parseDouble(ultimaTag.getLatitude());
                    double lonAnterior = Double.parseDouble(ultimaTag.getLongitude());

                    double distancia = calcularDistanciaEmMetros(latAnterior, lonAnterior, latAtual, lonAtual);

                    if (distancia > DISTANCIA_MINIMA_METROS || isNovaFuga) {
                        log.info("Cadastrar local da Tag com numero {}", tag.getNumero());
                        tagRepository.save(tag);
                        if (isNovaFuga) mandarEmailAlertaFuga(tag);
                    }
                } else {
                    tagRepository.save(tag);
                }
            } catch (NumberFormatException e) {
                log.error("Erro ao converter coordenadas para a Tag {}: {}", tag.getNumero(), e.getMessage());
            }
        } else {
            log.info("Primeiro registro da Tag com numero {}", tag.getNumero());
            tagRepository.save(tag);
            if (isNovaFuga) mandarEmailAlertaFuga(tag);
        }
    }

    public static double calcularDistanciaEmMetros(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RAIO_TERRA_METROS * c;
    }

    public boolean isForaDoLocalAutorizado(Tag tag, LocalCoordenadas localAutorizado) {
        if (tag.getLatitude() == null || localAutorizado.getLatitude() == null) {
            return false;
        }

        try {
            double latTag = Double.parseDouble(tag.getLatitude());
            double lonTag = Double.parseDouble(tag.getLongitude());

            double latLocal = Double.parseDouble(localAutorizado.getLatitude());
            double lonLocal = Double.parseDouble(localAutorizado.getLongitude());

            double distanciaDaBase = calcularDistanciaEmMetros(latTag, lonTag, latLocal, lonLocal);

            return distanciaDaBase > localAutorizado.getRaio();

        } catch (NumberFormatException | NullPointerException e) {
            log.error("Falha no cálculo da cerca virtual: dados inválidos.");
            return false;
        }
    }

    public void mandarEmailAlertaFuga(Tag tag) {
        Animal animal = tag.getAnimal();
        if (animal == null) return;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("tag.dog.tcc@gmail.com");

        Tutor tutor = tutorRepository.findByAnimais(animal);
        Funcionario funcionario = funcionarioRepository.findByAnimais(animal);
        List<Admin> admins = adminRepository.findAll();

        java.util.List<String> emailsDestino = new java.util.ArrayList<>();

        if (tutor != null && tutor.getEmail() != null) emailsDestino.add(tutor.getEmail());
        if (funcionario != null && funcionario.getEmail() != null) emailsDestino.add(funcionario.getEmail());
        admins.forEach(admin -> {
            if (admin.getEmail() != null) emailsDestino.add(admin.getEmail());
        });

        if (emailsDestino.isEmpty()) return;

        message.setTo(emailsDestino.toArray(new String[0]));
        message.setSubject("TagDog - Pet saiu sem autorização: " + animal.getNome());
        message.setText("O sistema detectou que o animal " + animal.getNome() + " (Tag: " + tag.getNumero() + ") "
                + "ultrapassou o perímetro de segurança configurado.\n\n"
                + "Acesse a plataforma imediatamente para verificar as coordenadas atuais e rastrear a localização.");

        log.info("Email de saida nao autorizada do Animal com matricula " + animal.getMatricula() + " mandada para " + Arrays.toString(emailsDestino.toArray(new String[0])));
//        mailSender.send(message);
    }

    public List<TagDTO.TagResponseDTO> buscarPosicoesAtuais() {
        log.info("Listar Tag atuais");
        return tagRepository.findUltimasPosicoesDeCadaTag()
                .stream()
                .map(TagDTO.TagResponseDTO::fromEntity)
                .toList();
    }
}