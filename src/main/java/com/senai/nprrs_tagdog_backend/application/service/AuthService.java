package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.AuthDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Usuario;
import com.senai.nprrs_tagdog_backend.domain.repository.UsuarioRepository;
import com.senai.nprrs_tagdog_backend.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeNaoEncontradaException;
import com.senai.nprrs_tagdog_backend.domain.exceptions.DadosInvalidosException;


@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public String login(AuthDTO.LoginRequest req) {
        Usuario usuario = usuarios.findByEmailAndAtivoTrue(req.email())
                .orElseThrow(() ->  new EntidadeNaoEncontradaException("Usuário"));

        if (!encoder.matches(req.senha(), usuario.getSenha())) {
            throw new DadosInvalidosException("Credenciais inválidas");
        }

        log.info("Login do usuário com email " + usuario.getEmail());
        return jwt.generateToken(usuario.getEmail(), usuario.getRole().name());
    }
}