package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.TagDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Animal;
import com.senai.nprrs_tagdog_backend.domain.entity.Tag;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeNaoEncontradaException;
import com.senai.nprrs_tagdog_backend.domain.exceptions.RegraNegocioException;
import com.senai.nprrs_tagdog_backend.domain.repository.AnimalRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {
    private final AnimalRepository animalRepository;
    private final TagRepository tagRepository;
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

        // Busca a última posição registrada deste dispositivo
        Optional<Tag> ultimaTagOpt = tagRepository.findFirstByNumeroOrderByDataCriadoDesc(dto.numero());

        if (ultimaTagOpt.isPresent()) {
            Tag ultimaTag = ultimaTagOpt.get();

            try {
                // Como seus campos são String, precisamos converter para double
                double latAtual = Double.parseDouble(tag.getLatitude());
                double lonAtual = Double.parseDouble(tag.getLongitude());
                double latAnterior = Double.parseDouble(ultimaTag.getLatitude());
                double lonAnterior = Double.parseDouble(ultimaTag.getLongitude());

                double distancia = calcularDistanciaEmMetros(
                        latAnterior, lonAnterior, latAtual, lonAtual
                );

                // Se o animal se moveu MAIS que a distância mínima, a tag será salva
                if (distancia > DISTANCIA_MINIMA_METROS) {
                    tagRepository.save(tag);
                }
            } catch (NumberFormatException e) {
                // Log de erro caso o rastreador envie uma string inválida/suja nas coordenadas
                throw new RegraNegocioException("Erro ao converter coordenadas: " + e.getMessage());
            }
        } else {
            tagRepository.save(tag);
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
}
