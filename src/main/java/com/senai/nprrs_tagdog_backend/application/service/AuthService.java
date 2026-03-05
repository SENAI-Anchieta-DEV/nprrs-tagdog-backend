package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.AuthDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Usuario;
import com.senai.nprrs_tagdog_backend.domain.exceptions.AcessoNegadoException;
import com.senai.nprrs_tagdog_backend.domain.repository.UsuarioRepository;
import com.senai.nprrs_tagdog_backend.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeNaoEncontradaException;
import com.senai.nprrs_tagdog_backend.domain.exceptions.DadosInvalidosException;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public String login(AuthDTO.LoginRequest req) {
        Usuario usuario = usuarios.findByEmailAndAtivoTrue(req.email())
                .orElseThrow(() ->  new EntidadeNaoEncontradaException("Usuário")); //EntidadeNaoEncontradaException("Usuário")

        if (!encoder.matches(req.senha(), usuario.getSenha())) {
            throw new DadosInvalidosException("Credenciais inválidas");
        }

        return jwt.generateToken(usuario.getEmail(), usuario.getRole().name());
    }
}