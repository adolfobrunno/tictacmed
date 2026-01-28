package com.abba.tictacmed.application.messaging.handler;

import com.abba.tictacmed.application.messaging.AIMessage;
import com.abba.tictacmed.application.messaging.flow.FlowState;

public interface MessageHandler {

    boolean supports(AIMessage message, FlowState state);

    void handle(AIMessage message, FlowState state);
}
