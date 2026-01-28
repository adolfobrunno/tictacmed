package com.abba.tictacmed.application.messaging.classifier;

import com.abba.tictacmed.application.dto.AiMessageProcessorDto;
import com.abba.tictacmed.application.messaging.AIMessage;
import com.abba.tictacmed.application.messaging.flow.FlowState;

public interface MessageClassifier {

    AiMessageProcessorDto classify(AIMessage message, FlowState state);
}
