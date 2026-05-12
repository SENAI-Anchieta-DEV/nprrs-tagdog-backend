package com.senai.nprrs_tagdog_backend.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.nprrs_tagdog_backend.application.dto.TagDTO;
import lombok.extern.log4j.Log4j2;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class NuvemSincronizadorService {

    private final TagService tagService;
    private final ObjectMapper objectMapper;

    public NuvemSincronizadorService(TagService tagService, ObjectMapper objectMapper) {
        this.tagService = tagService;
        this.objectMapper = objectMapper;
        conectarBrokerNuvem();
    }

    private void conectarBrokerNuvem() {
        try {
            MqttClient nuvemClient = new MqttClient("tcp://broker.emqx.io:1883", "backend-nuvem-manual");

            nuvemClient.connect();
            log.info("Conectado manualmente ao broker da nuvem!");

            nuvemClient.subscribe("0806meupet/rastreador/coordenadas", (topic, message) -> {
                String payload = new String(message.getPayload());
                TagDTO.TagRegistroDTO dto = objectMapper.readValue(payload, TagDTO.TagRegistroDTO.class);
                log.info("Dado da NUVEM recebido: " + dto);
                tagService.salvar(dto);
            });

        } catch (Exception e) {
            log.error("Erro ao conectar na nuvem: " + e.getMessage());
        }
    }
}