package com.senai.nprrs_tagdog_backend.domain.repository;

import com.senai.nprrs_tagdog_backend.domain.entity.Animal;
import com.senai.nprrs_tagdog_backend.domain.entity.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, String> {
    Tutor findByEmail(String email);
    Tutor findByCpf(String cpf);
    Tutor findByAnimais(Animal animal);
}
