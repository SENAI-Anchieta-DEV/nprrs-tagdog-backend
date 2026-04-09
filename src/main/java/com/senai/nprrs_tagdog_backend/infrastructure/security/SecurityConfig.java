package com.senai.nprrs_tagdog_backend.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private final UsuarioDetailsService usuarioDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors1 -> cors1.configurationSource(request -> {
                    var cors = new org.springframework.web.cors.CorsConfiguration();
                    cors.setAllowedOrigins(java.util.List.of("http://localhost:3000", "http://localhost:8080"));
                    cors.setAllowedMethods(java.util.List.of("GET","POST","PUT","DELETE","OPTIONS"));
                    cors.setAllowedHeaders(java.util.List.of("*"));
                    cors.setAllowCredentials(true);
                    return cors;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**", "/api/tags", "/actuator/**", "/").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/emailtoken/email/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/admin", "/api/local").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/tutores", "/api/funcionarios", "/api/animais").hasAnyRole("ADMIN", "FUNCIONARIO")
                        .requestMatchers(HttpMethod.GET, "/api/funcionarios/**", "/api/tutores/**", "/api/animais/**").hasAnyRole("ADMIN", "FUNCIONARIO", "TUTOR")
                        .requestMatchers(HttpMethod.GET, "/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/admin/**", "/api/funcionarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tutores/**", "/api/animais/**").hasAnyRole("ADMIN", "FUNCIONARIO")
                        .requestMatchers(HttpMethod.PUT, "/api/emailtoken").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/**", "/api/funcionarios/**", "/api/local/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/tutores/**", "/api/animais/**").hasAnyRole("ADMIN", "FUNCIONARIO")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(usuarioDetailsService);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
