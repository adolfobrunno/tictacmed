package com.abba.tictacmed.infrastructure.messaging.whatsapp;

import com.abba.tictacmed.application.patient.service.ConfirmationCodeSender;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WhatsAppConfirmationCodeSender implements ConfirmationCodeSender {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppConfirmationCodeSender.class);

    private final WhatsAppClient whatsAppClient;
    private final WhatsAppProperties properties;

    @Override
    public void sendCode(String contact, String code) {
        if (!properties.isEnabled()) {
            log.info("[WhatsApp disabled] Would send confirmation code {} to {}", code, contact);
            return;
        }
        String body = "Seu código de confirmação do TicTacMed é: " + code + ". Compartilhe com o atendente para finalizar seu cadastro.";
        whatsAppClient.sendText(contact, body);
    }
}
