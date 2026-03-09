package com.senai.nprrs_tagdog_backend.domain.repository;

import com.senai.nprrs_tagdog_backend.domain.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, String> {

    Optional<Animal> findByMatriculaAndAtivoTrue(String matricula);

    List<Animal> findAllByAtivoTrue();

}