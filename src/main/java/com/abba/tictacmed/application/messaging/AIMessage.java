package com.abba.tictacmed.application.messaging;

import com.abba.tictacmed.domain.model.MessageReceived;
import lombok.Data;

@Data
public class AIMessage {

    private String id;
    private String whatsappId;
    private String contactName;
    private String body;
    private String replyToId;

    public static AIMessage from(MessageReceived messageReceived) {
        AIMessage message = new AIMessage();
        message.setId(messageReceived.getId());
        message.setWhatsappId(messageReceived.getWhatsappId());
        message.setContactName(messageReceived.getContactName());
        message.setBody(messageReceived.getMessage());
        message.setReplyToId(messageReceived.getRepliedTo());
        return message;
    }
}
