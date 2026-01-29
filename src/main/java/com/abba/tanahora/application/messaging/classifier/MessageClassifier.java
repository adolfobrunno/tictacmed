package com.abba.tanahora.application.messaging.classifier;

import com.abba.tanahora.application.dto.AiMessageProcessorDto;
import com.abba.tanahora.application.messaging.AIMessage;
import com.abba.tanahora.application.messaging.flow.FlowState;

public interface MessageClassifier {

    AiMessageProcessorDto classify(AIMessage message, FlowState state);
}
