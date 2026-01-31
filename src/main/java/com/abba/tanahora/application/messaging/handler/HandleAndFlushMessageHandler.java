package com.abba.tanahora.application.messaging.handler;

import com.abba.tanahora.application.messaging.AIMessage;
import com.abba.tanahora.application.messaging.flow.FlowState;

public interface HandleAndFlushMessageHandler extends MessageHandler {

    void handleAndFlush(AIMessage message, FlowState state);

    default void handle(AIMessage message, FlowState state) {
        handleAndFlush(message, state);
        flush(state);
    }

    default void flush(FlowState state) {
        state.setCurrentFlow(null);
        state.setStep(null);
        state.getContext().clear();
    }
}
