package com.senai.nprrs_tagdog_backend.domain.repository;

import com.senai.nprrs_tagdog_backend.domain.entity.EmailToken;
import com.senai.nprrs_tagdog_backend.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailTokenRepository extends JpaRepository<EmailToken, String> {
    EmailToken findByUsuario(Usuario usuario);
}
