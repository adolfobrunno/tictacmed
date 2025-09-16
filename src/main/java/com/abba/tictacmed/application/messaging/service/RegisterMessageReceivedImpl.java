package com.abba.tictacmed.application.messaging.service;

import com.abba.tictacmed.application.messaging.command.RegisterMessageReceivedCommand;
import com.abba.tictacmed.application.messaging.usecases.RegisterMessageReceived;
import com.abba.tictacmed.domain.messaging.model.WhatsAppMessage;
import com.abba.tictacmed.domain.messaging.repository.WhatsAppMessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RegisterMessageReceivedImpl implements RegisterMessageReceived {

    private final WhatsAppMessageRepository whatsAppMessageRepository;

    @Transactional
    @Override
    public void execute(RegisterMessageReceivedCommand cmd) {
        Objects.requireNonNull(cmd, "cmd");
        WhatsAppMessage message = WhatsAppMessage.register(cmd.messageId(), cmd.from(), cmd.body());
        whatsAppMessageRepository.save(message);
    }
}
