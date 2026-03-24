package com.senai.nprrs_tagdog_backend.domain.repository;

import com.senai.nprrs_tagdog_backend.domain.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, String> {
    Optional<Animal> findByMatricula(String matricula);
    List<Animal> findAll();
    boolean existsByMatricula(String matricula);

    @Query("SELECT a FROM Animal a WHERE a.id NOT IN (SELECT anim.id FROM Funcionario f JOIN f.animais anim)")
    List<Animal> findAnimaisSemFuncionario();

    Animal findByNumeroTag(String numeroTag);
}