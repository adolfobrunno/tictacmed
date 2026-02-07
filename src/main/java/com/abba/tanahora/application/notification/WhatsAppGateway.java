package com.abba.tanahora.application.notification;

import com.abba.tanahora.domain.model.User;

public interface WhatsAppGateway {

    String sendMessage(User user, WhatsAppMessage message);

}
