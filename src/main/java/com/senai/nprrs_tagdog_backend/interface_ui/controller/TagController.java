package com.senai.nprrs_tagdog_backend.interface_ui.controller;

import com.rafaelcosta.spring_mqttx.domain.annotation.MqttPayload;
import com.rafaelcosta.spring_mqttx.domain.annotation.MqttSubscriber;
import com.senai.nprrs_tagdog_backend.application.dto.TagDTO;
import com.senai.nprrs_tagdog_backend.application.service.TagService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Tag", description = "Gerenciamento de tags")
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Log4j2
public class TagController {
    private final TagService tagService;

    @MqttSubscriber("0806meupet/rastreador/coordenadas")
    public void salvar(@MqttPayload TagDTO.TagRegistroDTO dto) {
        tagService.salvar(dto);
        log.info(dto);
    }
}
