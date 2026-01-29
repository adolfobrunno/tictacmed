package com.abba.tanahora.application.messaging.processor;

import com.abba.tanahora.application.messaging.AIMessage;
import com.abba.tanahora.application.messaging.flow.FlowState;
import com.abba.tanahora.application.messaging.handler.MessageHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageChain {

    private final List<MessageHandler> handlers;

    public MessageChain(List<MessageHandler> handlers) {
        this.handlers = handlers;
    }

    public boolean process(AIMessage message, FlowState state) {
        for (MessageHandler handler : handlers) {
            if (handler.supports(message, state)) {
                handler.handle(message, state);
                return true;
            }
        }
        return false;
    }
}
