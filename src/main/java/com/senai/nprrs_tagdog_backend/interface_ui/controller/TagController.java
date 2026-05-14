package com.senai.nprrs_tagdog_backend.interface_ui.controller;

//import com.rafaelcosta.spring_mqttx.domain.annotation.MqttPayload;
//import com.rafaelcosta.spring_mqttx.domain.annotation.MqttSubscriber;
import com.senai.nprrs_tagdog_backend.application.dto.TagDTO;
import com.senai.nprrs_tagdog_backend.application.dto.TutorDTO;
import com.senai.nprrs_tagdog_backend.application.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Tag", description = "Gerenciamento de tags")
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Log4j2
public class TagController {
    private final TagService tagService;

//    @MqttSubscriber("0806meupet/rastreador/coordenadas")
//    public void salvar(@MqttPayload TagDTO.TagRegistroDTO dto) {
//        tagService.salvar(dto);
//        log.info(dto);
//    }

    @Operation(
            summary = "Buscar a ultima tag recebida",
            description = "Retorna a tag mais recente dos animais",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tag retornada com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Tad não encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "Taf não encontrada", value = "\"Tag não encontrada.\"")
                            )
                    )
            }
    )
    @GetMapping("/posicoes-atuais")
    public ResponseEntity<List<TagDTO.TagResponseDTO>> buscarPosicoesAtuais() {
        return ResponseEntity.ok(tagService.buscarPosicoesAtuais());
    }
}
