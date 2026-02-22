package com.senai.nprrs_tagdog_backend.domain.repository;

import com.senai.nprrs_tagdog_backend.domain.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, String> {
    List<Funcionario> findAllByAtivoTrue();
    Optional<Funcionario> findByEmailAndAtivoTrue(String email);
}
