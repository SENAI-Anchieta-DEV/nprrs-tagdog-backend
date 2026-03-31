package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.TagDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.*;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeNaoEncontradaException;
import com.senai.nprrs_tagdog_backend.domain.exceptions.RegraNegocioException;
import com.senai.nprrs_tagdog_backend.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
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

        Animal animal = animalRepository.findByNumeroTag(dto.numero());
        if(animal != null){
            tag.setAnimal(animal);
        }

        if(!tag.isAtivo()){
            tag.setAtivo(true);
        }

        // 1. Busca as coordenadas do local autorizado
        LocalCoordenadas localCoordenadas = localCoordenadasRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Local Coordenadas"));

        // 2. Define se a tag atual está fora do limite
        if(isForaDoLocalAutorizado(tag, localCoordenadas)){
            tag.setSaidaNaoAutorizada(true);
        }

        // 3. Busca a última posição registrada deste dispositivo
        Optional<Tag> ultimaTagOpt = tagRepository.findFirstByNumeroOrderByDataCriadoDesc(dto.numero());

        // 4. Verifica se é uma nova fuga (transição de "dentro" para "fora")
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
                double latAtual = Double.parseDouble(tag.getLatitude());
                double lonAtual = Double.parseDouble(tag.getLongitude());
                double latAnterior = Double.parseDouble(ultimaTag.getLatitude());
                double lonAnterior = Double.parseDouble(ultimaTag.getLongitude());

                double distancia = calcularDistanciaEmMetros(
                        latAnterior, lonAnterior, latAtual, lonAtual
                );

                // 5. Salva se o animal se moveu mais que a distância mínima OU se acabou de fugir
                if (distancia > DISTANCIA_MINIMA_METROS || isNovaFuga) {
                    tagRepository.save(tag);

                    if (isNovaFuga) {
                        mandarEmailAlertaFuga(tag);
                    }
                }
            } catch (NumberFormatException e) {
                throw new RegraNegocioException("Erro ao converter coordenadas: " + e.getMessage());
            }
        } else {
            // Primeiro registro da tag
            tagRepository.save(tag);
            if (isNovaFuga) {
                mandarEmailAlertaFuga(tag);
            }
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
        try {
            double latTag = Double.parseDouble(tag.getLatitude());
            double lonTag = Double.parseDouble(tag.getLongitude());

            double latLocal = Double.parseDouble(localAutorizado.getLatitude());
            double lonLocal = Double.parseDouble(localAutorizado.getLongitude());

            double distanciaDaBase = calcularDistanciaEmMetros(latTag, lonTag, latLocal, lonLocal);

            return distanciaDaBase > localAutorizado.getRaio();

        } catch (NumberFormatException e) {
            throw new RegraNegocioException("Coordenadas inválidas para cálculo de cerca virtual.");
        }
    }

    public void mandarEmailAlertaFuga(Tag tag) {
        Animal animal = tag.getAnimal();
        if (animal == null) return; // Retorna rápido caso não tenha animal vinculado

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

        if (emailsDestino.isEmpty()) return; // Não tenta enviar se não tiver destinatários

        message.setTo(emailsDestino.toArray(new String[0]));
        message.setSubject("TagDog - Pet saiu sem autorização: " + animal.getNome());
        message.setText("O sistema detectou que o animal " + animal.getNome() + " (Tag: " + tag.getNumero() + ") "
                + "ultrapassou o perímetro de segurança configurado.\n\n"
                + "Acesse a plataforma imediatamente para verificar as coordenadas atuais e rastrear a localização.");

        mailSender.send(message);
    }
}