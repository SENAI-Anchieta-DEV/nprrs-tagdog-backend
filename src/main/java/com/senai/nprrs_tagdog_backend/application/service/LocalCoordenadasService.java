package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.LocalCoordenadasDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.LocalCoordenadas;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeNaoEncontradaException;
import com.senai.nprrs_tagdog_backend.domain.exceptions.RegraNegocioException;
import com.senai.nprrs_tagdog_backend.domain.repository.LocalCoordenadasRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class LocalCoordenadasService {

    private final LocalCoordenadasRepository localCoordenadasRepository;

    public LocalCoordenadasDTO registrar(LocalCoordenadasDTO dto) {
        if(!localCoordenadasRepository.findAll().isEmpty()){
            throw new RegraNegocioException("Coordenadas do local existe");
        }

        log.info("Cadastrar Local com cep " +  dto.cep());
        return LocalCoordenadasDTO.fromEntity(localCoordenadasRepository.save(dto.toEntity()));
    }

    public LocalCoordenadasDTO buscar() {
        log.info("Buscar Local");
        return LocalCoordenadasDTO.fromEntity(localCoordenadasRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Local Coordenadas")));
    }

    public void deletar(String cep) {
        LocalCoordenadas localCoordenadas = localCoordenadasRepository.findByCep(cep)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Local Coordenadas"));

        log.info("Deletar Local com cep " + cep);
        localCoordenadasRepository.delete(localCoordenadas);
    }

}