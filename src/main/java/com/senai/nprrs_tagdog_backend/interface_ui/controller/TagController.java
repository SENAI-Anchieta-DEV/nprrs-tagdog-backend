package com.senai.nprrs_tagdog_backend.interface_ui.controller;

import com.rafaelcosta.spring_mqttx.domain.annotation.MqttPayload;
import com.rafaelcosta.spring_mqttx.domain.annotation.MqttSubscriber;
import org.springframework.stereotype.Component;

@Component
public class TagController {
    @MqttSubscriber("topico/teste")
    public void receberMensagem(@MqttPayload String mensagem) {
        System.out.println("Mensagem recebida: " + mensagem);
    }
}
