package com.abba.tictacmed.infrastructure.messaging.whatsapp;

import com.abba.tictacmed.domain.messaging.model.WhatsAppMessage;
import com.abba.tictacmed.domain.messaging.repository.MessageRepository;
import com.abba.tictacmed.infrastructure.messaging.whatsapp.dto.MessageContext;
import com.abba.tictacmed.infrastructure.messaging.whatsapp.processor.SimpleProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class WhatsAppMessageProcessor {

    private final MessageReceivedResolver messageReceivedResolver;
    private final MessageRepository messageRepository;
    private final Set<SimpleProcessor> processors;

    @Scheduled(fixedDelayString = "${tictacmed.scheduler.fixed-delay-ms:60000}")
    public void processMessages() {

        Set<WhatsAppMessage> messages = messageRepository.findAllUnprocessed();

        for (WhatsAppMessage message : messages) {
            var extracted = messageReceivedResolver.readMessage(message.getBody());
            log.info("Message: {}", extracted);
            SimpleProcessor processor = processors.stream().filter(p -> p.resolveType() == extracted.type())
                    .findFirst()
                    .orElseThrow();
            processor.process(new MessageContext(
                    message.getSenderName(),
                    message.getFrom(),
                    extracted
            ));
            message.markProcessed();
            messageRepository.save(message);
        }


    }

}
