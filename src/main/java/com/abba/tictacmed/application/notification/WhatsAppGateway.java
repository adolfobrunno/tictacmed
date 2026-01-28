package com.abba.tictacmed.application.notification;

import com.abba.tictacmed.domain.model.User;

public interface WhatsAppGateway {

    enum WhatsAppMessageType {
        BASIC, BUTTONS
    }

    String sendMessage(User user, String message, WhatsAppMessageType type);

    default String sendMessage(User user, String message) {
        return sendMessage(user, message, WhatsAppMessageType.BASIC);
    }

}
