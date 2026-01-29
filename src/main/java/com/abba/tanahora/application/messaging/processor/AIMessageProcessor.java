package com.abba.tanahora.application.messaging.processor;

import com.abba.tanahora.application.messaging.AIMessage;
import com.abba.tanahora.application.messaging.flow.FlowState;
import com.abba.tanahora.application.messaging.flow.FlowStateRepository;
import com.abba.tanahora.domain.model.MessageReceived;
import com.abba.tanahora.domain.service.MessageReceivedHandler;
import org.springframework.stereotype.Service;

@Service
public class AIMessageProcessor implements MessageReceivedHandler {

    private final MessageChain messageChain;
    private final FlowStateRepository flowStateRepository;

    public AIMessageProcessor(MessageChain messageChain, FlowStateRepository flowStateRepository) {
        this.messageChain = messageChain;
        this.flowStateRepository = flowStateRepository;
    }

    @Override
    public void handle(MessageReceived messageReceived) {
        AIMessage message = AIMessage.from(messageReceived);
        String userId = message.getWhatsappId();
        FlowState state = flowStateRepository.load(userId);
        if (messageChain.process(message, state)) {
            flowStateRepository.save(state);
        }
    }

}
