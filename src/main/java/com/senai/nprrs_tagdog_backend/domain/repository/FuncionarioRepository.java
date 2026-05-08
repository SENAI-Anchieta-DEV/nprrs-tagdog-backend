package com.senai.nprrs_tagdog_backend.domain.repository;

import com.senai.nprrs_tagdog_backend.domain.entity.Animal;
import com.senai.nprrs_tagdog_backend.domain.entity.Funcionario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, String> {
    @EntityGraph(attributePaths = "animais")
    List<Funcionario> findAll();
    @EntityGraph(attributePaths = "animais")
    Optional<Funcionario> findByEmail(String email);
    Funcionario findByAnimais(Animal animal);
}
