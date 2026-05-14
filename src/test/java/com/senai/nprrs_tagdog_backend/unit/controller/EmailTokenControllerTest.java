package com.senai.nprrs_tagdog_backend.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.nprrs_tagdog_backend.application.dto.SenhaDTO;
import com.senai.nprrs_tagdog_backend.application.service.EmailTokenService;
import com.senai.nprrs_tagdog_backend.infrastructure.security.JwtAuthenticationFilter;
import com.senai.nprrs_tagdog_backend.infrastructure.security.JwtService;
import com.senai.nprrs_tagdog_backend.interface_ui.controller.EmailTokenController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailTokenController.class)
@AutoConfigureMockMvc(addFilters = false) // Desabilita filtros de segurança para focar na lógica do controller
public class EmailTokenControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailTokenService emailTokenService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("Deve validar token e alterar senha retornando status 204")
    void deveValidarTokenEAlterarSenha() throws Exception {

        SenhaDTO senhaDTO = new SenhaDTO(
                "tutor@email.com",
                "123456",
                "novaSenha123"
        );

        // Como o método do Service é 'void', configuramos o Mockito para não fazer nada (sucesso)
        doNothing().when(emailTokenService).validarEmailToken(any(SenhaDTO.class));

        mockMvc.perform(put("/api/emailtoken")
                        .with(csrf()) // Simula o token CSRF se necessário
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(senhaDTO)))
                .andExpect(status().isNoContent()); // Espera o status 204
    }
}

