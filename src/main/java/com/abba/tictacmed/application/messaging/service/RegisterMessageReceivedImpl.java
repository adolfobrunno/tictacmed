package com.abba.tictacmed.application.messaging.service;

import com.abba.tictacmed.application.messaging.command.RegisterMessageReceivedCommand;
import com.abba.tictacmed.application.messaging.usecases.RegisterMessageReceived;
import com.abba.tictacmed.domain.messaging.model.WhatsAppMessage;
import com.abba.tictacmed.domain.messaging.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RegisterMessageReceivedImpl implements RegisterMessageReceived {

    private final MessageRepository messageRepository;

    @Transactional
    @Override
    public void execute(RegisterMessageReceivedCommand cmd) {
        Objects.requireNonNull(cmd, "cmd");
        WhatsAppMessage message = WhatsAppMessage.register(cmd.messageId(), cmd.from(), cmd.contact(), cmd.body());
        messageRepository.save(message);
    }
}
