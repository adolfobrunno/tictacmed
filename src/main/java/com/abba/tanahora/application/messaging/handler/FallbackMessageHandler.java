package com.abba.tanahora.application.messaging.handler;

import com.abba.tanahora.application.messaging.AIMessage;
import com.abba.tanahora.application.messaging.flow.FlowState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1000)
@Slf4j
public class FallbackMessageHandler implements MessageHandler {

    @Override
    public boolean supports(AIMessage message, FlowState state) {
        return true;
    }

    @Override
    public void handle(AIMessage message, FlowState state) {
        log.info("No handler matched message id={} whatsappId={}", message.getId(), message.getWhatsappId());
    }
}
