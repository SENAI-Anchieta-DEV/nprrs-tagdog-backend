package com.senai.nprrs_tagdog_backend.domain.repository;

import com.senai.nprrs_tagdog_backend.domain.entity.LocalCoordenadas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocalCoordenadasRepository extends JpaRepository<LocalCoordenadas, String> {
    Optional<LocalCoordenadas> findByCep(String cep);
}
