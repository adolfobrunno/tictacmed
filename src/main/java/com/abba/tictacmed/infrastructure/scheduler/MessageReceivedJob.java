package com.abba.tictacmed.infrastructure.scheduler;

import com.abba.tictacmed.domain.model.MessageReceived;
import com.abba.tictacmed.domain.service.MessageReceivedHandler;
import com.abba.tictacmed.domain.service.MessageReceivedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(prefix = "tictacmed.scheduler", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MessageReceivedJob {

    private static final Logger log = LoggerFactory.getLogger(MessageReceivedJob.class);

    private final MessageReceivedService messageReceivedService;
    private final MessageReceivedHandler messageReceivedHandler;

    public MessageReceivedJob(MessageReceivedService messageReceivedService,
                              MessageReceivedHandler messageReceivedHandler) {
        this.messageReceivedService = messageReceivedService;
        this.messageReceivedHandler = messageReceivedHandler;
    }

    @Scheduled(fixedDelayString = "${tictacmed.scheduler.fixed-delay-ms:60000}")
    public void processPendingMessages() {
        log.debug("Processing pending messages");
        List<MessageReceived> pending = messageReceivedService.getPendingMessages();
        for (MessageReceived message : pending) {
            try {
                log.debug("Processing message id={} whatsappId={}", message.getId(), message.getWhatsappId());
                messageReceivedHandler.handle(message);
                messageReceivedService.markAsProcessed(message.getId());
            } catch (Exception e) {
                log.error("Failed to process message id={} whatsappId={}", message.getId(), message.getWhatsappId(), e);
            }
        }
    }
}
