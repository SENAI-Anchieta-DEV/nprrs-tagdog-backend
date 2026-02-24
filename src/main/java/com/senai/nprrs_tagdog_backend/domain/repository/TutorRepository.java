package com.senai.nprrs_tagdog_backend.domain.repository;

import com.senai.nprrs_tagdog_backend.domain.entity.Tutor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, String> {
    List<Tutor> findAllByAtivoTrue();
    Tutor findByEmailAndAtivoTrue(String email);
    Tutor findByCpfAndAtivoTrue(String cpf);

    boolean existsByEmail(@NotNull @NotBlank @Email String email);

    boolean existsByCpf(@NotNull @NotBlank String cpf);
}
