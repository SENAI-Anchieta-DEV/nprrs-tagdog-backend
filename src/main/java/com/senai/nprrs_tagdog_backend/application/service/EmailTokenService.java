package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.EmailTokenDTO;
import com.senai.nprrs_tagdog_backend.application.dto.SenhaDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.EmailToken;
import com.senai.nprrs_tagdog_backend.domain.entity.Usuario;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeNaoEncontradaException;
import com.senai.nprrs_tagdog_backend.domain.exceptions.RegraNegocioException;
import com.senai.nprrs_tagdog_backend.domain.repository.EmailTokenRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailTokenService {
    private final EmailTokenRepository emailTokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public EmailTokenDTO criarTokenEMandarEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário"));

        Random random = new Random();
        Integer token = random.nextInt(100000, 999999);

        EmailToken emailToken = new EmailToken();
        if(emailTokenRepository.findByUsuario(usuario) != null){
            emailToken = emailTokenRepository.findByUsuario(usuario);
            emailToken.setToken(token);
            emailToken.setDataExpirado(LocalDateTime.now().plusDays(1));
            emailTokenRepository.save(emailToken);
            mandarEmail(usuario, emailToken);
        } else {
            emailToken.setUsuario(usuario);
            emailToken.setToken(token);
            emailToken.setDataExpirado(LocalDateTime.now().plusDays(1));
            emailToken.setDataCriado(LocalDateTime.now());
            emailTokenRepository.save(emailToken);
            mandarEmail(usuario, emailToken);
        }
        return EmailTokenDTO.fromEntity(emailToken);
    }

    public void mandarEmail(Usuario usuario, EmailToken emailToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("tag.dog.tcc@gmail.com");
        message.setTo(usuario.getEmail());
        message.setSubject("TagDog - Redefinir a sua senha com o token " + emailToken.getToken());
        message.setText("Olá " + usuario.getNome() +
                "\n\nPara redefinir a sua senha use o token: "
                + "\n\n" + emailToken.getToken()
                + "\n\nO token irá expirar em 1 dia.");

        mailSender.send(message);
    }
}
