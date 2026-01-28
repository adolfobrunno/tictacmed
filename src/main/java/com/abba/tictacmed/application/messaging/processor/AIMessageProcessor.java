package com.abba.tictacmed.application.messaging.processor;

import com.abba.tictacmed.application.messaging.AIMessage;
import com.abba.tictacmed.application.messaging.flow.FlowState;
import com.abba.tictacmed.application.messaging.flow.FlowStateRepository;
import com.abba.tictacmed.domain.model.MessageReceived;
import com.abba.tictacmed.domain.service.MessageReceivedHandler;
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
