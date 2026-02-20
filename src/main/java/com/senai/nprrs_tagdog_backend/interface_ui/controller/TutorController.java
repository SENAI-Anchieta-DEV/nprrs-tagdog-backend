package com.senai.nprrs_tagdog_backend.interface_ui.controller;

import com.senai.nprrs_tagdog_backend.application.dto.TutorDTO;
import com.senai.nprrs_tagdog_backend.application.service.TutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Tutor", description = "Gerenciamento de tutores")
@RestController
@RequestMapping("/api/tutores")
@RequiredArgsConstructor
public class TutorController {
    private final TutorService tutorService;

    @Operation(
            summary = "Cadastrar um novo tutor",
            description = "Realiza o cadastro do tutor",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TutorDTO.TutorRegistroDTO.class),
                            examples = @ExampleObject(name = "Exemplo válido", value = """
                                        {
                                             "nome": "Nome",
                                             "email": "tutor@email.com",
                                             "senha": "senha",
                                             "cpf": "111.111.111-11",
                                             "telefone": "(11) 11111-1111",
                                             "endereco": {
                                                "cep": "111111-11",
                                                "numero": "11",
                                                "complemento": "Apartamento"
                                             },
                                             "animais": [
                                                {
                                                    "nome": "Bob",
                                                    "raca": "Golden Retriever",
                                                    "sexo": "MACHO",
                                                    "porte": "GRANDE",
                                                    "dataNascimento": "2026-02-19",
                                                    "descricao": "Alergia a chocolate"
                                                }
                                                {
                                                    "nome": "Bob 2",
                                                    "raca": "Golden Retriever",
                                                    "sexo": "MACHO",
                                                    "porte": "GRANDE",
                                                    "dataNascimento": "2026-02-19",
                                                    "descricao": "Alergia a chocolate"
                                                }
                                             ]
                                         }
                                    """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cadastro realizado com sucesso")
            }
    )
    @PostMapping
    public ResponseEntity<TutorDTO.TutorResponseDTO> registrarTutor(@Valid @RequestBody TutorDTO.TutorRegistroDTO dto) {
        TutorDTO.TutorResponseDTO novoTutor = tutorService.registrarTutor(dto);
        return ResponseEntity.created(
                URI.create("/api/tutores/email/" + novoTutor.email())
        ).body(novoTutor);
    }
}
