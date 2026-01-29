package com.abba.tanahora.application.messaging.handler;

import com.abba.tanahora.application.messaging.AIMessage;
import com.abba.tanahora.application.messaging.flow.FlowState;

public interface MessageHandler {

    boolean supports(AIMessage message, FlowState state);

    void handle(AIMessage message, FlowState state);
}
