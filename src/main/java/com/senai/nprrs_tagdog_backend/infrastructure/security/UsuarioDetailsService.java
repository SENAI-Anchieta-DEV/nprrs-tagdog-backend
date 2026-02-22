package com.senai.nprrs_tagdog_backend.infrastructure.security;

import com.senai.nprrs_tagdog_backend.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {
    private final UsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String cpf) throws RuntimeException { //EntidadeNaoEncontradaException
        var usuario = repository.findByEmail(cpf)
                .orElseThrow(() -> new RuntimeException()); //() -> new EntidadeNaoEncontradaException("usuário")

        return new User(
                usuario.getEmail(),
                usuario.getSenha(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRole()))
        );
    }
}
